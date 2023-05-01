package renderer;

public class MatrixMultiplication {
	
	public MatrixMultiplication() {
		
	}
	
	public int[][] Intmatmul(int[][] a, int[][] b){
		int colsA = a.length;
		int rowsA = a[0].length;
		int colsB = b.length;
		int rowsB = b[0].length;
		int[][] resultt = new int[colsA][rowsB];

		int sum = 0;
		
		if(rowsA != colsB) {
		return null;
		}else {
			for(int i = 0; i<colsA; i++) {
				for(int j = 0; j<rowsB; j++) {
					sum = 0;
					for(int k = 0; k<colsB; k++) {
						sum = sum + a[i][k] * b[k][j];
					}
					resultt[i][j] = sum;
				}
			}

			return resultt;
		}
	}
	
	public double[][] doublematmul(double[][] a, double[][] b){
		int colsA = a.length;
		int rowsA = a[0].length;
		int colsB = b.length;
		int rowsB = b[0].length;
		double[][] resultt = new double[colsA][rowsB];

		double sum = 0;
		
		if(rowsA != colsB) {
		return null;
		}else {
			for(int i = 0; i<colsA; i++) {
				for(int j = 0; j<rowsB; j++) {
					sum = 0;
					for(int k = 0; k<colsB; k++) {
						sum = sum + a[i][k] * b[k][j];
					}
					resultt[i][j] = sum;
				}
			}

			return resultt;
		}
	}
}
