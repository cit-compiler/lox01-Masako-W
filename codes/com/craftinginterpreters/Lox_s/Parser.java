package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /*
     * /
     * Expr parse() {
     * try {
     * return expression();
     * } catch (ParseError error) {
     * return null;
     * }
     * }
     */

    // �����̃X�e�[�g�����g����͂��ă��X�g�ɂ܂Ƃ߂�B
    // �g�[�N���̃��X�g���X�e�[�g�����g���Ƃɏ����B
    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            // statements.add(statement());
            statements.add(declaration());
        }

        return statements;
    }

    /*
     * ���iexpression�j����́B
     */
    private Expr expression() {
        // return equality();
        return assignment();
    }

    /*
     * �錾�i�ϐ��錾�Ȃǁj����́B
     * ���s���ɓ����������s���A���̈��S�Ȉʒu�܂ŃX�L�b�v�B
     */
    private Stmt declaration() {
        try {
            if (match(VAR))
                return varDeclaration();

            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    /*
     * ���i�X�e�[�g�����g�j����́B
     */
    private Stmt statement() {
        if (match(PRINT))
            return printStatement();
        if (match(LEFT_BRACE))
            return new Stmt.Block(block());

        return expressionStatement();
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(SEMICOLON, "Expect ';' after value.");
        return new Stmt.Print(value);
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect variable name.");

        Expr initializer = null;
        if (match(EQUAL)) {
            initializer = expression();
        }

        consume(SEMICOLON, "Expect ';' after variable declaration.");
        return new Stmt.Var(name, initializer);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new Stmt.Expression(expr);
    }

    private List<Stmt> block() {
        List<Stmt> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after block.");
        return statements;
    }

    private Expr assignment() {
        Expr expr = equality();

        if (match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if (expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invalid assignment target.");
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if (match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary() {
        if (match(FALSE))
            return new Expr.Literal(false);
        if (match(TRUE))
            return new Expr.Literal(true);
        if (match(NIL))
            return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }
        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect expression.");
    }

    /*
     * ���݂̃g�[�N��������̌^���m�F���Đi�߂�B
     */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    /*
     * �w�肵���g�[�N���^�����݂��Ȃ���΃G���[���X���[�B
     */

    private Token consume(TokenType type, String message) {
        if (check(type))
            return advance();

        throw error(peek(), message);
    }

    /*
     * �g�[�N���̌^���m�F�B
     */
    private boolean check(TokenType type) {
        if (isAtEnd())
            return false;
        return peek().type == type;
    }

    /*
     * ���̃g�[�N���ɐi�ށB
     */
    private Token advance() {
        if (!isAtEnd())
            current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    // �\���G���[�����������ۂɎg�p�����B
    private ParseError error(Token token, String message) {
        Lox.error(token, message);
        return new ParseError();
    }

    /*
     * ��������
     * �G���[�������ɍ\����͂𒆒f�����A���̕������͂𑱍s�\�ɂ���B
     */
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON)
                return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

}

/*
 * �R�[�h�S�̂̉���B
 * �\����͊�iParser
 * �g�[�N���̃��X�g����͂Ƃ��Ď󂯎��A�\���؁iAST: Abstract Syntax Tree�j�𐶐��B
 * �g�[�N��������̕��@�ɏ]���Ă��邩���m�F���A�v���O�����̍\�������߂���d�v�ȍH���B
 * 
 * ����: �g�[�N���̃��X�g�i�����͊� Scanner �ɂ���Đ����j�B
 * ����: �g�[�N������͂��A���@�ɏ]�����\���؂𐶐��B
 * �o��: �\���؁iAST�j�A�܂��͍\���G���[��񍐁B
 */