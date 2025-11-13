export EP_DB="127.0.0.1:5432"
export EP_NAME_DB="ep_bd"
export EP_DB_USER="postgres"
export EP_DB_PASS="1234"


# Compila os arquivos, mandando tudo para bin/
javac -d bin -cp lib/postgresql-42.7.8.jar $(find ./src -name "*.java")

# Executa
java -cp bin:lib/postgresql-42.7.8.jar main.java.clientapp.Main

rm -rf bin