package com.craftinginterpreters.lox;

class Token {
    final TokenType type; // �g�[�N���̎��
    final String lexeme; // �g�[�N���̌��ƂȂ镶����
    final Object literal; // �g�[�N���������e�����l�i�Ȃ��ꍇ�� null�j
    final int line; // �g�[�N�������������s�ԍ�

    /*
     * �g�[�N���I�u�W�F�N�g�����������邽�߂̃R���X�g���N�^�B
     * �e�t�B�[���h�ɒl��ݒ肷��B
     */
    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    /*
     * �g�[�N���𕶎���Ƃ��ĕ\�����邽�߂̃��\�b�h�B
     * �g�[�N���̎�ށA���̕�����A���e�����l��1�s�ł킩��₷���\������B
     */
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}

/*
 * �܂Ƃ�
 * ���̃R�[�h�̖���
 * �E�\�[�X�R�[�h�̒P���L������͂��č\������
 * �E�\����͂ɕK�v�ȏ���ێ�����
 * �E�f�o�b�O��G���[���|�[�g��e�Ղɂ���
 * �e�g�[�N���́A��ށA���ƂȂ镶����A���e�����l�A�s�ԍ��Ƃ�������ێ�����B
 */