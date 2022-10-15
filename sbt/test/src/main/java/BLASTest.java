
import org.junit.Assert;
import org.junit.Test;
import io.github.pityka.netlib.BLAS;
import io.github.pityka.netlib.LAPACK;
import java.util.Arrays;

public  class BLASTest {

  private static final  BLAS blas = BLAS.getInstance();
  private static LAPACK jLAPACK = LAPACK.getInstance();

  
  static public void offsets() {
    double[] matrix = new double[]{
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1,
        1, 1, 1, 1, 1
    };
    blas.dscal(5, 2.0, matrix, 2, 5);
    double[] expected = new double[]{
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1,
        1, 1, 2, 1, 1
    };
    Assert.assertArrayEquals(Arrays.toString(matrix), expected, matrix, 0.0);
  }

  
  static public void ddot() {
    double[] dx = {1.1, 2.2, 3.3, 4.4};
    double[] dy = {1.1, 2.2, 3.3, 4.4};
    int n = dx.length;

    double answer = blas.ddot(n, dx, 1, dy, 1);
    Assert.assertTrue(Math.abs(answer - 36.3) < 0.00001d);
  }

   
  static public void dgesvd() {
    double [] correct = new double[] {41.319080801703045, 4.036396452114566, 2.264037407750103, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    for (int i = 0; i < 1000; i++) {
      double[] jAns = testDgesvd1(jLAPACK);
      Assert.assertArrayEquals(correct, jAns, 0.00001);
    }
  }

  
  static public void dsygv() {
    double [] correct = new double []{0.38773165525286146, 1.0842530684697642, 2.3704016288236445};
    for (int i = 0; i < 1000; i++) {
      double[] jAns = testDsygv1(jLAPACK);
      Assert.assertArrayEquals(correct, jAns, 0.00001);
    }
  }

  static private double[] testDgesvd1(LAPACK lapack) {
    int M = 5;
    int N = 3;
    double[] m = {18.91, 14.91, -6.15, -18.15, 27.5, -1.59, -1.59, -2.25,
        -1.59, -2.25, -1.59, 1.59, 0.0, 1.59, 0.0
    };

    double[] s = new double[m.length];
    double[] u = new double[M * M];
    double[] vt = new double[N * N];
    double[] work =
        new double[Math.max(3 * Math.min(M, N) + Math.max(M, N),
            5 * Math.min(M, N))];
    org.netlib.util.intW info = new org.netlib.util.intW(2);

    lapack.dgesvd("A", "A", M, N, m, M, s, u, M, vt, N, work, work.length,
        info);

    return s;
  }

  static private double[] testDsygv1(LAPACK lapack) {
    int itype = 1;
    int n = 3;
    double[] a = {1.0, 2.0, 4.0, 0.0, 3.0, 5.0, 0.0, 0.0, 6.0};
    int lda = 3;
    double[] b = {2.5298, 0.6405, 0.2091, 0.3798, 2.7833, 0.6808, 0.4611,
        0.5678, 2.7942
    };
    int ldb = 3;
    double[] w = new double[n];
    int lwork = 9;
    double[] work = new double[lwork];
    org.netlib.util.intW info = new org.netlib.util.intW(0);

    lapack.dsygv(itype, "N", "U", n, a, lda, b, ldb, w, work, lwork, info);
    return w;
  }

  public static void main(String[] args) {
    BLASTest.offsets();
    BLASTest.ddot();
    BLASTest.dgesvd();
    BLASTest.dsygv();
    System.out.println("OK");
  }
}
