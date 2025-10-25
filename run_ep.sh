export EP_DB="ip e porta"
export EP_NAME_DB="nome BD"
export EP_DB_USER="usuario BD (default: postgres)"
export EP_DB_PASS="Senha do user"

# Cria o bin/ se necessário
mkdir -p bin

# Compila os arquivos, mandando tudo para bin/
javac -d bin -cp lib/postgresql-42.7.8.jar src/main/java/EpApplication.java

# Executa
java -cp bin:lib/postgresql-42.7.8.jar main.java.EpApplication

# Limpa bin/ ao fim da execução
rm -rf bin
