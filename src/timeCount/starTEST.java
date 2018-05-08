package timeCount;

public class starTEST {
   public static void main(String args[]) {
	   int center=4;
	   int i=0;
	   int width=0;
	   int count =0;
		   
	  while(count<5) {
	  while(i<7) {
		  i++;
		  if(i==center-width||i==center+width) {
			  System.out.print("*");
		  }
		
		  width++;
		  System.out.print("_");
		  
	  }
	  i=0;
	  width=0;
	  System.out.println();
	  count++;
	  }
	   
}
}