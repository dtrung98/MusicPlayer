package com.ldt.musicr.coordinate;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * Created by trung on 0017 17 Jan 2018.
 */

public class MCoordinate {
    public MCoordinate() {}
    public static class MPoint {
        public double[] toArray() {
            return new double[] {X,Y};
        }
        public MPoint() {
            X = Y = 0;
        }
        public MPoint(double x,double y) {
            X = x;
            Y = y;
        }
        public double X;

        public double getX() {
            return X;
        }

        public void setX(double x) {
            X = x;
        }

        public double getY() {
            return Y;
        }

        public void setY(int y) {
            Y = y;
        }

        public double Y;
    }
    public static class MLine {
        public MLine(double a,double b,double c) {
            A = a;
            B = b;
            C = c;
        }
        public double getA() {
            return A;
        }

        public void setA(double a) {
            this.A = a;
        }

        public double getB() {
            return B;
        }

        public void setB(double b) {
            this.B = b;
        }

        public double getC() {
            return C;
        }

        public void setC(double c) {
            this.C = c;
        }

        // 5x + 3y = 0
        // ax+ by = C
        double A, B, C;
        public double[] VectoPhapTuyen() {
            return new double[] {A,B};
        }
        public double[] VectoChiPhuong() {
            if(A<0) return new double[] {-A,B};
            return new double[] {A,-B};
        }
        public MLine(MPoint p1, MPoint p2)  {
            double[] vtcp = {
                    p2.X - p1.X,p2.Y - p1.Y
            };
            double[] vtpt = chuyenDoiHuong(vtcp);
            A = vtpt[0];
            B = vtpt[1];
            C = A*p1.X + B*p1.Y;
        }

        public MLine(MLine lSongSong, MPoint p) {
            double[] vtpt = lSongSong.VectoPhapTuyen();
            A = vtpt[0];
            B = vtpt[1];
            C = A*p.X + B*p.Y;
        }
        public MLine(double[] vtpt,MPoint p) {
            A = vtpt[0];
            B = vtpt[1];
            C = A*p.X + B*p.Y;
        }
        public MLine(double[] vtcp,MPoint p,Boolean IsVtcp) {
            double[] vtpt = chuyenDoiHuong(vtcp);
            A = vtpt[0];
            B = vtpt[1];
            C = A*p.X + B*p.Y;
        }
        public MLine DuongThangVuongGoc(MPoint p) {
            double[] vtpt = VectoChiPhuong();
            return new MLine(vtpt,p);
        }
       // public MPoint[] DiemCachMoc(MPoint p) {
//
       // }
        public MPoint GiaoDiem(MLine l) {
            //a1.x+b1.y= c1
            //a2.x+b2.y= c2
            // y = (c1 - a1.x)/b1
            // a2.x + (b2/b1)* (c1- a1.x) = c2
            // (a2 - b2.a1/b1).x + b2.c1/b1 = c2
            // x = (c2 - b2.c1/b1)/ (a2 - b2.a1/b1)
            MPoint p = new MPoint();
            p.X = (l.C - l.B*C/B)/(l.A - l.B*A/B);
            p.Y = (C - A*p.X)/B;
            return p;
        }
        public double[] toArray() {
            return new double[] {A,B,C};
        }

    }

    @NonNull
    @Contract(pure = true)
    public static double[] chuyenDoiHuong(double[] someTypeVector) {
        if(someTypeVector[0]<0)
            return new double[] {-someTypeVector[0],someTypeVector[1]};
        return new double[] {someTypeVector[0],-someTypeVector[1]};
    }


}
