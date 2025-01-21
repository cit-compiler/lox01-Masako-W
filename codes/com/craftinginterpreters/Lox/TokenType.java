package com.craftinginterpreters.lox;

enum TokenType {
    // Single-character tokens. 単一文字のトークン
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens. 1文字または2文字のトークン
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals. リテラル（リテラル値）
    IDENTIFIER, STRING, NUMBER,

    // Keywords. キーワード
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // ファイル終端を表すトークン
    EOF
}

/*
 * まとめ
 * このコードは、Lox言語で解析されるトークンの種類をすべて列挙したもの。
 * TokenTypeはLox言語で使用するトークンの種類を定義したもの。
 * Lox言語のインタプリタにおいて、コードを解析（トークン化）する際に重要な役割を果たす。
 */