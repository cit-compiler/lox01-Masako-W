//�p�b�P�[�W�錾�ƁA�K�v�ȕW�����C�u�������C���|�[�g
package com.craftinginterpreters.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
//import java.util.Scanner;     �����Ascanner.scanTokens�̎ז�������B
import java.util.Scanner;

public class Lox {
    // �G���[�������������ǂ�����ǐՂ���t���O�B�G���[��������true�B
    private static final Interpreter interpreter = new Interpreter();
    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    /*
     * �C���^�v���^�̃G���g���|�C���g�B�R�}���h���C�������������B
     * if���͈����̐����Ƃ̏ꍇ�������B
     * 2�ȏ�F�G���[�����A1�F�t�@�C����ǂݍ��ݎ��s�A0�FrunPrompt�ցB
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
     * �t�@�C����ǂݍ��݁A����s���e�� run �ɓn���B
     * �G���[����������G���[�R�[�h65�ŏI�������B
     * �����^�C���G���[�̏ꍇ�A�G���[�R�[�h70�ŏI���B
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
     * �W�����͂��g���đΘb���[�h�����s�B
     * 1�s���R�[�h����͂��� run ���\�b�h�ŏ����B
     * �e�s�̏�����ɃG���[��Ԃ����Z�b�g�B
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
     * source�i���͂��ꂽ�R�[�h�j���X�L���i�[�ɓn���ăg�[�N��������B
     * �g�[�N�������ꂽ���ʁitokens�j��1���o�́B
     * �p�[�T�[ (Parser) ��p���ĕ��@��͂��s���A���ۍ\���� (AST) �ɕϊ����܂��B
     * �C���^�v���^ (Interpreter) ��ʂ��ĉ�͌��ʂ����s���܂��B
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
     * ����̍s�ɃG���[�����������ꍇ�ɌĂяo����鏈���B
     */
    static void error(int line, String message) {
        report(line, "", message);
    }

    /*
     * �G���[���e���ڍׂɕW���G���[�o�͂ɕ\�����A
     * hadError �t���O�� true �ɐݒ�B
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
 * �܂Ƃ�
 * ���̃R�[�h�́ALox����C���^�v���^�̊�{�����B��ȋ@�\�͈ȉ��̒ʂ�B
 * �E�X�N���v�g���t�@�C��������s����@�\�B
 * �E�Θb�I�ɃR�[�h����͂��Ď��s����@�\�B
 * �E���͂��g�[�N���ɕ������A������o�͂���@�\�B
 * �E�G���[�����o���ă��[�U�[�ɕ񍐂���@�\�B
 */