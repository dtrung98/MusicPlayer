package com.ldt.musicr.ui.widget.fragmentnavigationcontroller;


import com.ldt.musicr.R;

/**
 * Created by burt on 2016. 5. 26..
 */
public class PresentStyle {

    public static final int NONE                    = 0;
    public static final int ACCORDION_LEFT          = 1;
    public static final int ACCORDION_RIGHT         = 2;
    public static final int ACCORDION_UP            = 3;
    public static final int ACCORDION_DOWN          = 4;
    public static final int CARD_FLIP_LEFT          = 5;
    public static final int CARD_FLIP_RIGHT         = 6;
    public static final int CARD_FLIP_UP            = 7;
    public static final int CARD_FLIP_DOWN          = 8;
    public static final int CUBE_LEFT               = 9;
    public static final int CUBE_RIGHT              = 10;
    public static final int CUBE_UP                 = 11;
    public static final int CUBE_DOWN               = 12;
    public static final int FADE                    = 13;
    public static final int GLIDE                   = 14;
    public static final int ROTATE_DOWN_LEFT        = 15;
    public static final int ROTATE_DOWN_RIGHT       = 16;
    public static final int ROTATE_UP_LEFT          = 17;
    public static final int ROTATE_UP_RIGHT         = 18;
    public static final int ROTATE_LEFT_UP          = 19;
    public static final int ROTATE_LEFT_DOWN        = 20;
    public static final int ROTATE_RIGHT_UP         = 21;
    public static final int ROTATE_RIGHT_DOWN       = 22;
    public static final int SCALEX                  = 23;
    public static final int SCALEY                  = 24;
    public static final int SCALEXY                 = 25;
    public static final int SLIDE_LEFT              = 26;
    public static final int SLIDE_RIGHT             = 27;
    public static final int SLIDE_UP                = 28;
    public static final int SLIDE_DOWN              = 29;
    public static final int STACK_LEFT              = 30;
    public static final int STACK_RIGHT             = 31;
    public static final int TABLE_LEFT              = 32;
    public static final int TABLE_RIGHT             = 33;
    public static final int TABLE_UP                = 34;
    public static final int TABLE_DOWN              = 35;
    public static final int ZOOM_FROM_LEFT_TOP_CORNER    = 36;
    public static final int ZOOM_FROM_RIGHT_TOP_CORNER   = 37;
    public static final int ZOOM_FROM_LEFT_BOTTOM_CORNER = 38;
    public static final int ZOOM_FROM_RIGHT_BOTTOM_CORNER= 39;


    private int openEnterAnimatorId;
    private int openExitAnimatorId;
    private int closeEnterAnimatorId;
    private int closeExitAnimatorId;


    public PresentStyle(int openEnterAnimatorId, int openExitAnimatorId, int closeEnterAnimatorId, int closeExitAnimatorId) {
        this.openEnterAnimatorId = openEnterAnimatorId;
        this.openExitAnimatorId = openExitAnimatorId;
        this.closeEnterAnimatorId = closeEnterAnimatorId;
        this.closeExitAnimatorId = closeExitAnimatorId;
    }

    public int getOpenEnterAnimatorId() {
        return openEnterAnimatorId;
    }

    public int getOpenExitAnimatorId() {
        return openExitAnimatorId;
    }

    public int getCloseEnterAnimatorId() {
        return closeEnterAnimatorId;
    }

    public int getCloseExitAnimatorId() {
        return closeExitAnimatorId;
    }

    public static PresentStyle get(int style) {
        switch (style) {
            case NONE:
                return None();
            case ACCORDION_LEFT:
                return AccordionLeft();
            case ACCORDION_RIGHT:
                return AccordionRight();
            case ACCORDION_UP:
                return AccordionUp();
            case ACCORDION_DOWN:
                return AccordionDown();
            case CUBE_LEFT:
                return CubeLeft();
            case CUBE_RIGHT:
                return CubeRight();
            case CUBE_UP:
                return CubeUp();
            case CUBE_DOWN:
                return CubeDown();
            case FADE:
                return Fade();
            case CARD_FLIP_LEFT:
                return CardFlipLeft();
            case CARD_FLIP_RIGHT:
                return CardFlipRight();
            case CARD_FLIP_UP:
                return CardFlipUp();
            case CARD_FLIP_DOWN:
                return CardFlipDown();
            case GLIDE:
                return Glide();
            case ROTATE_DOWN_LEFT:
                return RotateDownLeft();
            case ROTATE_DOWN_RIGHT:
                return RotateDownRight();
            case ROTATE_UP_LEFT:
                return RotateUpLeft();
            case ROTATE_UP_RIGHT:
                return RotateUpRight();
            case ROTATE_LEFT_UP:
                return RotateLeftUp();
            case ROTATE_LEFT_DOWN:
                return RotateLeftDown();
            case ROTATE_RIGHT_UP:
                return RotateRightUp();
            case ROTATE_RIGHT_DOWN:
                return RotateRightDown();
            case SCALEX:
                return ScaleX();
            case SCALEY:
                return ScaleY();
            case SCALEXY:
                return ScaleXY();
            case SLIDE_LEFT:
                return SlideLeft();
            case SLIDE_RIGHT:
                return SlideRight();
            case SLIDE_UP:
                return SlideUp();
            case SLIDE_DOWN:
                return SlideDown();
            case STACK_LEFT:
                return StackLeft();
            case STACK_RIGHT:
                return StackRight();
            case TABLE_LEFT:
                return TableLeft();
            case TABLE_RIGHT:
                return TableRight();
            case TABLE_UP:
                return TableUp();
            case TABLE_DOWN:
                return TableDown();
            case ZOOM_FROM_LEFT_TOP_CORNER:
                return ZoomFromLeftTopCorner();
            case ZOOM_FROM_LEFT_BOTTOM_CORNER:
                return ZoomFromLeftBottomCorner();
            case ZOOM_FROM_RIGHT_TOP_CORNER:
                return ZoomFromRightTopCorner();
            case ZOOM_FROM_RIGHT_BOTTOM_CORNER:
                return ZoomFromRightBottomCorner();
            default:
                return None();
        }
    }

    private static PresentStyle None() {
        return new PresentStyle(R.animator.none, R.animator.none, R.animator.none, R.animator.none);
    }

    private static PresentStyle AccordionLeft() {
        return new PresentStyle(R.animator.accordion_right_in, R.animator.accordion_left_out, R.animator.accordion_left_in, R.animator.accordion_right_out);
    }

    private static PresentStyle AccordionRight() {
        return new PresentStyle(R.animator.accordion_left_in, R.animator.accordion_right_out, R.animator.accordion_right_in, R.animator.accordion_left_out);
    }

    private static PresentStyle AccordionUp() {
        return new PresentStyle(R.animator.accordion_vertical_right_in, R.animator.accordion_vertical_left_out, R.animator.accordion_vertical_left_in, R.animator.accordion_vertical_right_out);
    }

    private static PresentStyle AccordionDown() {
        return new PresentStyle(R.animator.accordion_vertical_left_in, R.animator.accordion_vertical_right_out, R.animator.accordion_vertical_right_in, R.animator.accordion_vertical_left_out);
    }

    private static PresentStyle Fade() {
        return new PresentStyle(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out);
    }

    private static PresentStyle CubeLeft() {
        return new PresentStyle(R.animator.cube_right_in, R.animator.cube_left_out, R.animator.cube_left_in, R.animator.cube_right_out);
    }

    private static PresentStyle CubeRight() {
        return new PresentStyle(R.animator.cube_left_in, R.animator.cube_right_out, R.animator.cube_right_in, R.animator.cube_left_out);
    }

    private static PresentStyle CubeUp() {
        return new PresentStyle(R.animator.cube_vertical_right_in, R.animator.cube_vertical_left_out, R.animator.cube_vertical_left_in, R.animator.cube_vertical_right_out);
    }

    private static PresentStyle CubeDown() {
        return new PresentStyle(R.animator.cube_vertical_left_in, R.animator.cube_vertical_right_out, R.animator.cube_vertical_right_in, R.animator.cube_vertical_left_out);
    }

    private static PresentStyle CardFlipLeft() {
        return new PresentStyle(R.animator.card_flip_horizontal_right_in, R.animator.card_flip_horizontal_left_out, R.animator.card_flip_horizontal_left_in, R.animator.card_flip_horizontal_right_out);
    }

    private static PresentStyle CardFlipRight() {
        return new PresentStyle(R.animator.card_flip_horizontal_left_in, R.animator.card_flip_horizontal_right_out, R.animator.card_flip_horizontal_right_in, R.animator.card_flip_horizontal_left_out);
    }

    private static PresentStyle CardFlipDown() {
        return new PresentStyle(R.animator.card_flip_vertical_right_in, R.animator.card_flip_vertical_left_out, R.animator.card_flip_vertical_left_in, R.animator.card_flip_vertical_right_out);
    }

    private static PresentStyle CardFlipUp() {
        return new PresentStyle(R.animator.card_flip_vertical_left_in, R.animator.card_flip_vertical_right_out, R.animator.card_flip_vertical_right_in, R.animator.card_flip_vertical_left_out);
    }

    private static PresentStyle Glide() {
        return new PresentStyle(R.animator.glide_fragment_horizontal_in, R.animator.glide_fragment_horizontal_out, R.animator.glide_fragment_horizontal_in, R.animator.glide_fragment_horizontal_out);
    }

    private static PresentStyle RotateDownLeft() {
        return new PresentStyle(R.animator.rotatedown_right_in, R.animator.rotatedown_left_out, R.animator.rotatedown_left_in, R.animator.rotatedown_right_out);
    }

    private static PresentStyle RotateDownRight() {
        return new PresentStyle(R.animator.rotatedown_left_in, R.animator.rotatedown_right_out, R.animator.rotatedown_right_in, R.animator.rotatedown_left_out);
    }

    private static PresentStyle RotateUpLeft() {
        return new PresentStyle(R.animator.rotateup_right_in, R.animator.rotateup_left_out, R.animator.rotateup_left_in, R.animator.rotateup_right_out);
    }

    private static PresentStyle RotateUpRight() {
        return new PresentStyle(R.animator.rotateup_left_in, R.animator.rotateup_right_out, R.animator.rotateup_right_in, R.animator.rotateup_left_out);
    }

    private static PresentStyle RotateLeftUp() {
        return new PresentStyle(R.animator.rotateleft_right_in, R.animator.rotateleft_left_out, R.animator.rotateleft_left_in, R.animator.rotateleft_right_out);
    }

    private static PresentStyle RotateLeftDown() {
        return new PresentStyle(R.animator.rotateleft_left_in, R.animator.rotateleft_right_out, R.animator.rotateleft_right_in, R.animator.rotateleft_left_out);
    }

    private static PresentStyle RotateRightUp() {
        return new PresentStyle(R.animator.rotateright_right_in, R.animator.rotateright_left_out, R.animator.rotateright_left_in, R.animator.rotateright_right_out);
    }

    private static PresentStyle RotateRightDown() {
        return new PresentStyle(R.animator.rotateright_left_in, R.animator.rotateright_right_out, R.animator.rotateright_right_in, R.animator.rotateright_left_out);
    }

    private static PresentStyle ScaleX() {
        return new PresentStyle(R.animator.scalex_enter, R.animator.scalex_exit, R.animator.scalex_enter, R.animator.scalex_exit);
    }

    private static PresentStyle ScaleY() {
        return new PresentStyle(R.animator.scaley_enter, R.animator.scaley_exit, R.animator.scaley_enter, R.animator.scaley_exit);
    }

    private static PresentStyle ScaleXY() {
        return new PresentStyle(R.animator.scalexy_enter, R.animator.scalexy_exit, R.animator.scalexy_enter, R.animator.scalexy_exit);
    }

    private static PresentStyle SlideLeft() {
        return new PresentStyle(R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out, R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out);
    }

    private static PresentStyle SlideRight() {
        return new PresentStyle(R.animator.slide_fragment_horizontal_left_in, R.animator.slide_fragment_horizontal_right_out, R.animator.slide_fragment_horizontal_right_in, R.animator.slide_fragment_horizontal_left_out);
    }

    private static PresentStyle SlideUp() {
        return new PresentStyle(R.animator.slide_fragment_vertical_right_in, R.animator.slide_fragment_vertical_left_out, R.animator.slide_fragment_vertical_left_in, R.animator.slide_fragment_vertical_right_out);
    }

    private static PresentStyle SlideDown() {
        return new PresentStyle(R.animator.slide_fragment_vertical_left_in, R.animator.slide_fragment_vertical_right_out, R.animator.slide_fragment_vertical_right_in, R.animator.slide_fragment_vertical_left_out);
    }

    private static PresentStyle StackLeft() {
        return new PresentStyle(R.animator.stack_right_in, R.animator.stack_left_out, R.animator.stack_left_in, R.animator.stack_right_out);
    }

    private static PresentStyle StackRight() {
        return new PresentStyle(R.animator.stack_right_in, R.animator.slide_fragment_horizontal_right_out, R.animator.slide_fragment_horizontal_right_in, R.animator.stack_right_out);
    }

    private static PresentStyle TableLeft() {
        return new PresentStyle(R.animator.table_horizontal_right_in, R.animator.table_horizontal_left_out, R.animator.table_horizontal_left_in, R.animator.table_horizontal_right_out);
    }

    private static PresentStyle TableRight() {
        return new PresentStyle(R.animator.table_horizontal_left_in, R.animator.table_horizontal_right_out, R.animator.table_horizontal_right_in, R.animator.table_horizontal_left_out);
    }

    private static PresentStyle TableUp() {
        return new PresentStyle(R.animator.table_vertical_right_in, R.animator.table_vertical_left_out, R.animator.table_vertical_left_in, R.animator.table_vertical_right_out);
    }

    private static PresentStyle TableDown() {
        return new PresentStyle(R.animator.table_vertical_left_in, R.animator.table_vertical_right_out, R.animator.table_vertical_right_in, R.animator.table_vertical_left_out);
    }

    private static PresentStyle ZoomFromLeftTopCorner() {
        return new PresentStyle(R.animator.zoom_from_left_corner_right_in, R.animator.zoom_from_left_corner_left_out, R.animator.zoom_from_left_corner_left_in, R.animator.zoom_from_left_corner_right_out);
    }

    private static PresentStyle ZoomFromLeftBottomCorner() {
        return new PresentStyle(R.animator.zoom_from_right_corner_left_in, R.animator.zoom_from_right_corner_right_out, R.animator.zoom_from_right_corner_right_in, R.animator.zoom_from_right_corner_left_out);
    }

    private static PresentStyle ZoomFromRightTopCorner() {
        return new PresentStyle(R.animator.zoom_from_right_corner_right_in, R.animator.zoom_from_right_corner_left_out, R.animator.zoom_from_right_corner_left_in, R.animator.zoom_from_right_corner_right_out);
    }

    private static PresentStyle ZoomFromRightBottomCorner() {
        return new PresentStyle(R.animator.zoom_from_left_corner_left_in, R.animator.zoom_from_left_corner_right_out, R.animator.zoom_from_left_corner_right_in, R.animator.zoom_from_left_corner_left_out);
    }

}
