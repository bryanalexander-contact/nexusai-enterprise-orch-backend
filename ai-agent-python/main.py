import os
import shutil
from fastapi import FastAPI, UploadFile, File, HTTPException
from pydantic import BaseModel
from dotenv import load_dotenv

# LangChain Imports
from langchain_google_genai import ChatGoogleGenerativeAI, GoogleGenerativeAIEmbeddings
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import Chroma
from prometheus_fastapi_instrumentator import Instrumentator
load_dotenv()

app = FastAPI(title="NexusAI Agent Service - FinOps Gateway")

# 1. Configuración de Modelos
llm = ChatGoogleGenerativeAI(model="gemini-1.5-flash") # Ajustar a 2.5 cuando esté disponible
embeddings = GoogleGenerativeAIEmbeddings(model="models/embedding-001")

# 2. Configuración de ChromaDB
# Nos conectamos al contenedor que definimos en el compose.yaml
vector_db = Chroma(
    persist_directory="./chroma_db",
    embedding_function=embeddings,
    collection_name="nexusai_knowledge"
)

Instrumentator().instrument(app).expose(app)

class QueryRequest(BaseModel):
    user_query: str
    user_id: int

@app.get("/")
def read_root():
    return {"status": "AI Agent Service is Running", "engine": "Gemini 2.5 Ready"}

@app.post("/v1/ai/process")
async def process_ai(request: QueryRequest):
    try:
        # Búsqueda en la base de datos vectorial (RAG)
        # Buscamos los 3 fragmentos más relevantes para la pregunta
        docs = vector_db.similarity_search(request.user_query, k=3)
        context = "\n".join([doc.page_content for doc in docs])
        
        # Construimos el Prompt enriquecido
        prompt = f"""
        Eres un asistente experto de NexusAI. 
        Usa el siguiente contexto para responder la pregunta del usuario. 
        Si no sabes la respuesta basándote en el contexto, dilo, pero no inventes.
        
        CONTEXTO:
        {context}
        
        PREGUNTA:
        {request.user_query}
        """
        
        response = llm.invoke(prompt)
        
        return {
            "response": response.content,
            "user_id": request.userId,
            "cost": 0.05,
            "source_documents_count": len(docs)
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.post("/v1/agent/upload-doc")
async def upload_document(file: UploadFile = File(...)):
    try:
        # Guardar archivo temporalmente
        temp_path = f"temp_{file.filename}"
        with open(temp_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
        
        # 1. Cargar el PDF
        loader = PyPDFLoader(temp_path)
        documents = loader.load()
        
        # 2. Fragmentar el texto (Chunking)
        # Dividimos en trozos de 1000 caracteres con un solapamiento para no perder contexto
        text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)
        chunks = text_splitter.split_documents(documents)
        
        # 3. Guardar en ChromaDB
        vector_db.add_documents(chunks)
        
        # Limpieza
        os.remove(temp_path)
        
        return {
            "filename": file.filename, 
            "chunks_processed": len(chunks),
            "status": "stored_in_vector_db"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))