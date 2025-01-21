//パッケージ宣言と、必要な標準ライブラリをインポート
package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
//import java.util.Scanner;     多分、scanner.scanTokensの邪魔をする。
import java.util.Scanner;

public class Lox {
    // エラーが発生したかどうかを追跡するフラグ。エラー発生時＝true。
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    /*
     * インタプリタのエントリポイント。コマンドライン引数を処理。
     * if文は引数の数ごとの場合を処理。
     * 2以上：エラー処理、1：ファイルを読み込み実行、0：runPromptへ。
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    /*
     * ファイルを読み込み、そのs内容を run に渡す。
     * エラー発生したらエラーコード65で終了処理。
     * ランタイムエラーの場合、エラーコード70で終了。
     */
    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        // Indicate an error in the exit code.
        if (hadError)
            System.exit(65);
        if (hadRuntimeError)
            System.exit(70);
    }

    /*
     * 標準入力を使って対話モードを実行。
     * 1行ずつコードを入力して run メソッドで処理。
     * 各行の処理後にエラー状態をリセット。
     */
    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null)
                break;
            run(line);
            hadError = false;
        }
    }

    /*
     * source（入力されたコード）をスキャナーに渡してトークン化する。
     * トークン化された結果（tokens）を1つずつ出力。
     * パーサー (Parser) を用いて文法解析を行い、抽象構文木 (AST) に変換します。
     * インタプリタ (Interpreter) を通じて解析結果を実行します。
     */
    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        // Expr expression = parser.parse();
        List<Stmt> statements = parser.parse();

        // Stop if there was a syntax error.
        if (hadError)
            return;
        // interpreter.interpret(expression);
        interpreter.interpret(statements);
        // System.out.println(new AstPrinter().print(expression));

        // For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token);
        }
    }

    /*
     * 特定の行にエラーが発生した場合に呼び出される処理。
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /*
     * エラー内容を詳細に標準エラー出力に表示し、
     * hadError フラグを true に設定。
     */
    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void error(Token token, String message) {
        if (token.type == TokenType.EOF) {
            report(token.line, " at end", message);
        } else {
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() +
                "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }

}

/*
 * まとめ
 * このコードは、Lox言語インタプリタの基本部分。主な機能は以下の通り。
 * ・スクリプトをファイルから実行する機能。
 * ・対話的にコードを入力して実行する機能。
 * ・入力をトークンに分解し、それを出力する機能。
 * ・エラーを検出してユーザーに報告する機能。
 */