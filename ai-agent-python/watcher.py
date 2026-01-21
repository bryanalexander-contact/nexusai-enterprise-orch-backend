import os
import time
from watchdog.observers import Observer
from watchdog.events import FileSystemEventHandler
from dotenv import load_dotenv

# LangChain e infraestructura
from langchain_google_genai import GoogleGenerativeAIEmbeddings
from langchain_community.document_loaders import PyPDFLoader
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_community.vectorstores import Chroma

load_dotenv()

# Configuración idéntica a main.py para que compartan la base de datos
embeddings = GoogleGenerativeAIEmbeddings(model="models/embedding-001")
vector_db = Chroma(
    persist_directory="/app/chroma_db", # <--- Usa la ruta absoluta del contenedor
    embedding_function=embeddings,
    collection_name="nexusai_knowledge"
)

def process_pdf(file_path):
    """La misma lógica que tenías en tu endpoint, pero automática"""
    try:
        print(f"--- Iniciando procesamiento de: {file_path} ---")
        
        # 1. Cargar el PDF
        loader = PyPDFLoader(file_path)
        documents = loader.load()
        
        # 2. Fragmentar el texto
        text_splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=100)
        chunks = text_splitter.split_documents(documents)
        
        # 3. Guardar en ChromaDB
        vector_db.add_documents(chunks)
        
        print(f"--- Éxito: {len(chunks)} fragmentos añadidos a la base vectorial ---")
    except Exception as e:
        print(f"Error procesando {file_path}: {e}")

class DocHandler(FileSystemEventHandler):
    def on_created(self, event):
        if not event.is_directory and event.src_path.endswith(".pdf"):
            # Pequeña pausa para asegurar que el archivo terminó de copiarse
            time.sleep(2) 
            process_pdf(event.src_path)

if __name__ == "__main__":
    DOCS_PATH = "/app/docs"
    
    # Crear la carpeta si no existe dentro del contenedor
    if not os.path.exists(DOCS_PATH):
        os.makedirs(DOCS_PATH)

    print(f"Ojo Avizor: Vigilando la carpeta {DOCS_PATH}...")
    
    event_handler = DocHandler()
    observer = Observer()
    observer.schedule(event_handler, DOCS_PATH, recursive=False)
    observer.start()
    
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        observer.stop()
    observer.join()