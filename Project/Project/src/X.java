import java.util.Random;

public class X extends Expression {
 
	@Override
    public double evaluate(double x, double y) {
       return x;
    }
 
	public Expression variation(int maxDepth, Random random) {
		if (random.nextBoolean()) {
			return Expression.random(maxDepth, random);
		} else {
			return this;
		}
	}

}
