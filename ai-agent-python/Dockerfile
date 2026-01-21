FROM python:3.11-slim
WORKDIR /app
# Copiamos requerimientos e instalamos
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
# Copiamos el resto del código
COPY . .
# Exponemos el puerto de FastAPI
EXPOSE 8000
CMD python watcher.py & uvicorn main:app --host 0.0.0.0 --port 8000