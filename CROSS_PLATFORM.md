# Configuração Cross-Platform

## Problema Resolvido
Alguns textos e componentes ficavam invisíveis no Ubuntu devido a diferenças no Look and Feel (LAF) entre macOS e Linux.

## Solução Implementada

### 1. Look and Feel Padrão
**Arquivo**: `src/main/java/clientapp/Main.java`

Configurado o **Metal Look and Feel** (padrão Java) que funciona de forma consistente em todos os sistemas operacionais:

```java
UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
```

Isso garante que a interface tenha a mesma aparência em macOS, Linux e Windows.

### 2. Classe Utilitária UIHelper
**Arquivo**: `src/main/java/gui/UIHelper.java`

Criada classe utilitária com métodos para:
- Configurar cores explícitas em todos os componentes
- Criar botões com estilo consistente
- Garantir visibilidade de labels, campos de texto, listas e tabelas
- Método `ensureVisibility()` que percorre recursivamente todos os componentes

### 3. Cores Explícitas
Todos os componentes agora têm cores explicitamente definidas:
- **Textos/Labels**: `Color.BLACK` (preto)
- **Campos de entrada**: Background `Color.WHITE` (branco), Foreground `Color.BLACK`
- **Painéis**: Background `Color(240, 240, 240)` (cinza claro)
- **Botões**: Cores customizadas com `setOpaque(true)` e `setBorderPainted(false)`

### 4. Garantia de Visibilidade
Todos os frames agora chamam `UIHelper.ensureVisibility(this)` após inicialização, garantindo que:
- Labels tenham texto preto
- Campos de texto tenham fundo branco e texto preto
- Listas e tabelas sejam visíveis
- Todos os componentes sejam renderizados corretamente

## Benefícios
✅ Interface consistente entre macOS, Linux e Windows
✅ Textos sempre visíveis independente do sistema
✅ Aparência profissional e limpa
✅ Sem dependência de LAF específico do sistema

## Testado em
- ✅ macOS (Aqua LAF nativo → Metal LAF)
- ✅ Ubuntu/Linux (GTK LAF nativo → Metal LAF)
