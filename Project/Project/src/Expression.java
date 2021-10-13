import java.util.Random;


public abstract class Expression {

	public abstract double evaluate(double x, double y);
 
    public abstract Expression variation(int maxDepth, Random random);
 
    public static Expression random(int maxDepth, Random random) {
       if(maxDepth == 0) {
    	   int dieNum = random.nextInt(5) + 1;
    	   
    	   if(dieNum <= 2) {
    		   return new X();
    	   } else if(dieNum <= 4) {
    		  return new Y();
    	   } else {
    		   return new K();
    	   }
    	   
       } else {
    	   int dieNum = random.nextInt(17) + 1;
    	   
    	   if(dieNum <= 3) {
    		   return new Sin(Expression.random(maxDepth - 1, random));
    	   } else if(dieNum <= 6) {
    		   return new Cos(Expression.random(maxDepth - 1, random));
    	   } else if(dieNum <= 12) {
    		   return new Avg(Expression.random(maxDepth - 1, random), Expression.random(maxDepth - 1, random));
    	   } else if(dieNum <= 14) {
    		   return new X();
    	   } else if(dieNum <= 16) {
    		   return new Y();
    	   } else {
    		   return new K();
    	   }
       }
    }
 
}