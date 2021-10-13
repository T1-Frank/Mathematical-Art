import java.util.Random;

public class Prod extends Expression {
	
	private Expression sub1, sub2;
	
	public Prod(Expression exp1, Expression exp2) {
		sub1 = exp1;
		sub2 = exp2;
	}
 
	@Override
    public double evaluate(double x, double y) {
       return sub1.evaluate(x, y) * sub2.evaluate(x, y);
    }
 
	public Expression variation(int maxDepth, Random random) {
		int dieNum = random.nextInt(6) + 1;
		
		if(dieNum == 1) {
			return Expression.random(maxDepth, random);
		} else if(dieNum <= 3) {
			return new Prod(sub1.variation(maxDepth - 1, random), sub2);
		} else if(dieNum <= 5) {
			return new Prod(sub1, sub2.variation(maxDepth - 1, random));
		} else {
			return this;
		}
	}

}