import java.util.Random;

public class Y extends Expression {
 
	@Override
    public double evaluate(double x, double y) {
       return y;
    }
 
	public Expression variation(int maxDepth, Random random) {
		if (random.nextBoolean()) {
			return Expression.random(maxDepth, random);
		} else {
			return this;
		}
	}

}