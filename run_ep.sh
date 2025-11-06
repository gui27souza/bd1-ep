export EP_DB="ip e porta"
export EP_NAME_DB="nome BD"
export EP_DB_USER="usuario BD (default: postgres)"
export EP_DB_PASS="Senha do user"


# Compila os arquivos, mandando tudo para bin/
javac -d . -cp lib/postgresql-42.7.8.jar src/main/java/*.java

# Executa
java -cp .:lib/postgresql-42.7.8.jar main.java.Main

rm -rf main