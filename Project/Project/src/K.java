import java.util.Random;

public class K extends Expression{
 
	private double constant;
	
    public K() {
    	Random random = new Random();
    	constant = random.nextDouble() * 2 - 1;
    }
    
    @Override
    public double evaluate(double x, double y) {
    	return constant;
    }
    
    @Override
    public Expression variation(int maxDepth, Random random) {
    	if (random.nextBoolean()) {
			return Expression.random(maxDepth, random);
		} else {
			return this;
		}
    }

}