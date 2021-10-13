import java.util.Random;

public class Cos extends Expression {
 
	private Expression sub;
	
	public Cos(Expression exp) {
		sub = exp;
	}
	
	@Override
    public double evaluate(double x, double y) {
       return Math.cos(Math.PI * sub.evaluate(x, y));
    }
 
	public Expression variation(int maxDepth, Random random) {
		int dieNum = random.nextInt(5) + 1;
		
		if(dieNum == 1) {
			return Expression.random(maxDepth, random);
		} else if (dieNum <= 3){
			return sub.variation(maxDepth - 1, random);
		} else if(dieNum == 4) {
			return new Sin(sub);
		} else {
			return this;
		}
	}

}