import java.util.List;
import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CSegment;
import fr.lri.swingstates.canvas.CShape;
import fr.lri.swingstates.canvas.Canvas;

@SuppressWarnings("unused")
public class MagneticGuide extends CExtensionalTag{
	
	
	private CSegment cs;
	
	private Canvas c ;
	
	private List<CShape> parents;
	
	public MagneticGuide(Canvas c){
		super(c);
		this.c = c;
	}
	
	public void removed(CShape s) { 
		
	}
	
	public void added(CShape s){
		
	}
}
