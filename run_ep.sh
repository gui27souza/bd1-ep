# Cria o bin/ se necessário
mkdir -p bin

# Compila os arquivos, mandando tudo para bin/
javac -d bin -cp lib/postgresql-42.7.8.jar src/main/java/EpApplication.java

# Executa
java -cp bin:lib/postgresql-42.7.8.jar EpApplication

# Limpa bin/ ao fim da execução
rm -rf bin
