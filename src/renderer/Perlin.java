package renderer;

public class Perlin {
	
	static double persistence = 1/4;
	static int Number_Of_Octaves = 4;
	
	public static double Noise(int x, int y) {
	    int n = x + y * 57;
	    n = (n<<13) ^ n;
	    return ( 1.0 - ((n * (n * n * 15731 + 789221) + 1376312589) & 2147483647) / 1073741824.0);  
	}
	
	public static double SmoothNoise(double x, double y) {
		double corners = ( Noise((int)x-1, (int)y-1)+Noise((int)x+1, (int)y-1)+Noise((int)x-1, (int)y+1)+Noise((int)x+1, (int)y+1) ) / 16;
	    double sides   = ( Noise((int)x-1, (int)y)  +Noise((int)x+1, (int)y)  +Noise((int)x, (int)y-1)  +Noise((int)x, (int)y+1) ) /  8;
		double center  =  Noise((int)x, (int)y) / 4;
		return corners + sides + center;
	}
	
	public static double InterpolatedNoise(double x, double y) {
		int integer_X    = (int)x;
	    double fractional_X = x - integer_X;

		int integer_Y    = (int)y;
		double fractional_Y = y - integer_Y;

		double v1 = SmoothNoise(integer_X,     integer_Y);
	    double v2 = SmoothNoise(integer_X + 1, integer_Y);
		double v3 = SmoothNoise(integer_X,     integer_Y + 1);
		double v4 = SmoothNoise(integer_X + 1, integer_Y + 1);

		double i1 = Interpolate(v1 , v2 , fractional_X);
		double i2 = Interpolate(v3 , v4 , fractional_X);

		return Interpolate(i1 , i2 , fractional_Y);
	}
	
	public static double Interpolate(double a, double b, double x) {
		double ft = x * 3.1415927;
		double f = (1 - Math.cos(ft)) * .5;

		return  a*(1-f) + b*f;
	}
	
	public static double PerlinNoise(double x, double y) {
		double total = 0;
		double p = persistence;
		int n = Number_Of_Octaves - 1;

		for(int i = 0; i<n; i++) {
			double frequency = 2^i;
			double amplitude = Math.pow(p, i);
			total = total + InterpolatedNoise(x * frequency, y * frequency) * amplitude;
		}

		return total;
	}
}
