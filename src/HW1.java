import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;

public class HW1 {

	public static void main(String[] args)
	{
		if(args.length != 2 || args[0].isEmpty() || args[1].isEmpty() )
		{
			System.out.println(args.length);
			System.out.println("Please enter the file names correctly.");
			return;
		}
		CrossValidation validate = new CrossValidation();
		validate.readFirstFile(args[0]);
		validate.generateInput(args[1]);
		
		validate.nearestNeighbours();
	}
}
