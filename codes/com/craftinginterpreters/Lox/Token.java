package com.craftinginterpreters.lox;

class Token {
    final TokenType type; // トークンの種類
    final String lexeme; // トークンの元となる文字列
    final Object literal; // トークンが持つリテラル値（ない場合は null）
    final int line; // トークンが発生した行番号

    /*
     * トークンオブジェクトを初期化するためのコンストラクタ。
     * 各フィールドに値を設定する。
     */
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    /*
     * トークンを文字列として表現するためのメソッド。
     * トークンの種類、元の文字列、リテラル値を1行でわかりやすく表示する。
     */
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}

/*
 * まとめ
 * このコードの役割
 * ・ソースコードの単語や記号を解析して構成する
 * ・構文解析に必要な情報を保持する
 * ・デバッグやエラーレポートを容易にする
 * 各トークンは、種類、元となる文字列、リテラル値、行番号という情報を保持する。
 */