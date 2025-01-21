package com.craftinginterpreters.lox;

enum TokenType {
    // Single-character tokens. �P�ꕶ���̃g�[�N��
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    // One or two character tokens. 1�����܂���2�����̃g�[�N��
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals. ���e�����i���e�����l�j
    IDENTIFIER, STRING, NUMBER,

    // Keywords. �L�[���[�h
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // �t�@�C���I�[��\���g�[�N��
    EOF
}

/*
 * �܂Ƃ�
 * ���̃R�[�h�́ALox����ŉ�͂����g�[�N���̎�ނ����ׂė񋓂������́B
 * TokenType��Lox����Ŏg�p����g�[�N���̎�ނ��`�������́B
 * Lox����̃C���^�v���^�ɂ����āA�R�[�h����́i�g�[�N�����j����ۂɏd�v�Ȗ������ʂ����B
 */