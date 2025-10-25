export EP_DB=127.0.0.1:5432
export EP_NAME_DB=epdb
export EP_DB_USER=epuser
export EP_DB_PASS=12345

# Cria o bin/ se necessário
mkdir -p bin

# Compila os arquivos, mandando tudo para bin/
javac -d bin -cp lib/postgresql-42.7.8.jar src/main/java/EpApplication.java

# Executa
java -cp bin:lib/postgresql-42.7.8.jar main.java.EpApplication

# Limpa bin/ ao fim da execução
rm -rf bin
