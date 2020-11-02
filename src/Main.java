import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.function.Function;

class Lexeme {
    private final String value;
    private final TypeLex type;

    Lexeme(String value, TypeLex type) {
        this.value = value;
        this.type = type;
    }

    public String GetValue() {
        return value;
    }

    public TypeLex GetType() {
        return type;
    }

    @Override
    public String toString() {
        return value + " - " + type.GetName();
    }
};

class TypeLex {
    private final String name;
    private final String[] tokens;
    private Function<String, Boolean> method;

    TypeLex(String name, String[] tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    TypeLex(String name, Function<String, Boolean> method) {
        this(name, (String[]) null);
        this.method = method;
    }

    public String GetName() {
        return name;
    }

    public Boolean identify(String lex) {
        if (tokens != null) {
            for (String token : tokens)
                if (lex.equals(token))
                    return true;
        } else
            return method.apply(lex);

        return false;
    }
}

public class Main {
    private boolean IdentifierSolve(String lex) {
        if (!(Character.isLetter(lex.charAt(0)) || lex.startsWith("_")))
            return false;

        for (char ch : lex.toCharArray())
            if (!(Character.isLetterOrDigit(ch) || lex.startsWith("_")))
                return false;

        return true;
    }

    private boolean HEXADECIMALNumberSolve(String lex) {
        lex = lex.startsWith("-") ? lex.substring(1) : lex;
        lex = lex.startsWith("0") ? lex.substring(1) : null;
        if (lex == null) return false;
        lex = lex.toLowerCase().startsWith("x") ? lex.substring(1) : null;
        if (lex == null) return false;
        for (char ch : lex.toCharArray())
            if (!(Character.isDigit(ch)))
                return false;

        return true;
    }

    private boolean DecimalNumberSolve(String lex) {
        for (char ch : lex.startsWith("-") ? lex.substring(1).toCharArray() : lex.toCharArray())
            if (!(Character.isDigit(ch)))
                return false;

        return true;
    }

    private boolean FloPoNumberSolve(String lex) { //TODO:THINK ABOUT LOGIC
        lex = lex.startsWith("-") ? lex.substring(1) : lex;
        boolean dot = false;
        for (char ch : lex.toCharArray()) {
            if (!(Character.isDigit(ch))) {
                if (!dot && ch == '.')
                    dot = true;
                else
                    return false;
            }
        }
        return true;
    }

    private final TypeLex[] TypesLex = new TypeLex[]{
            new TypeLex("Operator", new String[]{"+", "-", "*", "/", "%", "=", "-=", "/=", "*=", "%=", "==", "<", ">", "!=", "&&", ">=", "+=", ";"}),
            new TypeLex("Comment", new String[]{"//"}),
            new TypeLex("Reserved", new String[]{"int", "long", "string", "double", "float", "void", "return", "break", "as", "is", "delegate", "continue", "using", "namespace", "static", "null"}),
            new TypeLex("Preprocessor", new String[]{"#include", "#define", "#ifdef", "#ifndef", "#endif"}),
            new TypeLex("Bracket", new String[]{"{", "}", ")", "(", "{}", "()"}),
            new TypeLex("Identifier", this::IdentifierSolve),
            new TypeLex("Hexadecimal", this::HEXADECIMALNumberSolve),
            new TypeLex("Decimal", this::DecimalNumberSolve),
            new TypeLex("Floating-point", this::FloPoNumberSolve),
    };

    private void PrintInFile(Lexeme lexeme, FileWriter FWrt) throws Exception {
        FWrt.write(lexeme.toString() + "\n");
    }

    public void Solve() throws Exception {
        Scanner scanner = new Scanner(new File("in.txt"));
        FileWriter FWrt = new FileWriter(new File("out.txt"));
        while (scanner.hasNext()) {
            Lexeme lexeme = new Lexeme(scanner.next(), null);

            for (TypeLex currTypeLex : TypesLex) {
                if (currTypeLex.identify(lexeme.GetValue())) {
                    lexeme = new Lexeme(lexeme.GetValue(), currTypeLex);
                    break;
                }
            }
            if (lexeme.GetType() == null) {
                lexeme = new Lexeme(lexeme.GetValue(), new TypeLex("Error", new String[]{}));
            }
            if (lexeme.GetType().GetName() == "Comment") {
                scanner.nextLine();
                continue;
            }
            PrintInFile(lexeme, FWrt);
        }
        FWrt.close();
        scanner.close();
        System.out.println("Ready");
    }

    public static void main(String[] args) throws Exception {
        new Main().Solve();
    }
}
