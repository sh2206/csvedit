import javax.swing.JFrame;

public class CSVEdit
{	
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("CSV Editor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		CSVPanel panel = new CSVPanel();
		frame.getContentPane().add(panel);
		
		frame.pack();
		frame.setVisible(true);
	}
}