provider "aws" {
  region = "us-east-1"
}

resource "aws_instance" "nexus_ai_server" {
  ami           = "ami-0c55b159cbfafe1f0" # Amazon Linux 2
  instance_type = "t3.medium"

  tags = {
    Name = "NexusAI-FinOps-Server"
    Project = "NexusAI"
  }
}

# Definición de la base de datos RDS para PostgreSQL
resource "aws_db_instance" "nexus_db" {
  allocated_storage    = 20
  engine               = "postgres"
  instance_class       = "db.t3.micro"
  db_name              = "nexusai"
  username             = "myuser"
  password             = "mypassword"
  skip_final_snapshot  = true
}