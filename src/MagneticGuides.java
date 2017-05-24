import fr.lri.swingstates.canvas.*;
import fr.lri.swingstates.canvas.Canvas;
import fr.lri.swingstates.debug.StateMachineVisualization;
import fr.lri.swingstates.sm.BasicInputStateMachine;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Drag;
import fr.lri.swingstates.sm.transitions.Press;
import fr.lri.swingstates.sm.transitions.Release;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

/**
 * @author Nicolas Roussel (roussel@lri.fr)
 *
 */
@SuppressWarnings({"unused", "serial"})
public class MagneticGuides extends JFrame {

	private Canvas canvas ;
	private CExtensionalTag oTag ;
	private CExtensionalTag magnet, horizontalMagnet, verticalMagnet;

	public MagneticGuides(String title, int width, int height) {
		super(title) ;
		canvas = new Canvas(width, height) ;
		canvas.setAntialiased(true) ;
		getContentPane().add(canvas) ;

		oTag = new CExtensionalTag(canvas) {} ;

		magnet =  new MagneticGuide(canvas) {};

		horizontalMagnet = new MagneticGuide(canvas) {};
		verticalMagnet =  new MagneticGuide(canvas) {};

		CStateMachine sm = new CStateMachine() {

			private Point2D p ;
			private CSegment seg;
			private CShape draggedShape, parent ;

			public State start = new State() {
				/* press a square */
				Transition pressOnTag = new PressOnTag(oTag, BUTTON1, ">> oDrag") {
					public void action() {
						p = getPoint() ;
						draggedShape = getShape() ;
					}
				} ;

				/* click on blank space with mouse left-button to create horizontal line */
				Transition newHorizontalLine = new Press(BUTTON1, ">> start"){
					public void action(){
						p = getPoint();
						seg = canvas.newSegment(0,p.getY(),canvas.getWidth(),p.getY());
						seg.addTag(horizontalMagnet);
						seg.addTag(magnet);
						seg.belowAll();
						seg.setOutlinePaint(null);
						System.out.println("New horizontal line");
						
					}
				};
				/* click on blank space with mouse right-button to create vertical line */
				Transition newVerticalLine = new Press(BasicInputStateMachine.BUTTON3, ">> start"){
					public void action(){
						p = getPoint();
						seg = canvas.newSegment(p.getX(),0,p.getX(),canvas.getHeight());
						seg.addTag(verticalMagnet);
						seg.addTag(magnet);
						seg.belowAll();
						seg.setOutlinePaint(null);
						System.out.println("New vertical line");
					}
				};

				/* drag horizontal line */
				Transition dragHLine = new PressOnTag(horizontalMagnet, BUTTON1, ">> DragHLine"){
					public void action(){
						p = getPoint();
						draggedShape = getShape();
						System.out.println("Press on tag Hmagnet: " + getShape());
						List<CShape> l = magnet.getFilledShapes();
						Iterator<CShape> it = l.iterator();
						CShape aux = null;
						while(it.hasNext()){
							CShape s = it.next();
							/* take only squares */
							if(s.hasTag(oTag)){
								System.out.println(s);
								if(s.contains(s.getMinX(), draggedShape.getCenterY()) != null){
									if(s.getParent() != draggedShape){
										double x = s.getCenterX(), y = s.getCenterY();
										draggedShape.addChild(s);
										s.translateTo(x, y);
									}
									System.out.println("the one: " + s + "/" +draggedShape);
								}
							}
						}
					}
				};

				/* drag vertical line */
				Transition dragVLine = new PressOnTag(verticalMagnet, BUTTON1, ">> DragVLine"){
					public void action(){
						p = getPoint();
						draggedShape = getShape();
						System.out.println("Press on tag Vmagnet: " + getShape());
						List<CShape> l = magnet.getFilledShapes();
						Iterator<CShape> it = l.iterator();
						CShape aux = null;
						while(it.hasNext()){
							CShape s = it.next();
							/* take only suares */
							if(s.hasTag(oTag)){
								System.out.println(s);
								if(s.contains(draggedShape.getCenterX(), s.getMinY()) != null){
									if(s.getParent() != draggedShape){
										double x = s.getCenterX(), y = s.getCenterY();
										draggedShape.addChild(s);
										s.translateTo(x, y);
									}
									System.out.println("the one: " + s + "/" +draggedShape);
								}
							}
						}
					}
				};


				/* click on ctrl+mouse left-button to remove horizontal line */
				Transition supprHLine = new ClickOnTag(horizontalMagnet, BUTTON1, CONTROL,">> start"){
					public void action(){
						getShape().removeTag(horizontalMagnet);
						getShape().removeTag(magnet);
						getShape().remove();
						System.out.println("HRemoved, I guess?");
					}
				};

				/* click on ctrl+mouse left-button to remove vertical line */
				Transition supprVLine = new ClickOnTag(verticalMagnet, BUTTON1, CONTROL, ">> start"){
					public void action(){
						getShape().removeTag(verticalMagnet);
						getShape().removeTag(magnet);
						getShape().remove();
						System.out.println("VRemoved, I guess?");
					}
				};

				/* add green color on hover line */
				Transition enterLine = new EnterOnTag(magnet){
					public void action(){
						if(!getShape().hasTag(oTag)){
							parent = getShape();
							System.out.println("EnterOnShape: "+parent);
							parent.setOutlinePaint(Color.green);
						}
					}
				};

				/* remove color line */
				Transition leaveLine = new LeaveOnTag(magnet){
					public void action(){
						if(!getShape().hasTag(oTag)){
							parent.setOutlinePaint(null);
							parent = null;
							System.out.println("LeaveOnShape: "+parent);
						}
					}
				};
			} ;

			/* drag state machine */
			public State oDrag = new State() {
				/* drag square */
				Transition drag = new Drag(BUTTON1,">> oDrag") {
					public void action() {
						Point2D q = getPoint() ;
						draggedShape.translateBy(q.getX() - p.getX(), q.getY() - p.getY()) ;
						p = q ;
					}
				} ;


				Transition enterLine = new EnterOnTag(magnet){
					public void action(){
						if(!getShape().hasTag(oTag)){
							parent = getShape();
							System.out.println("EnterOnShape: "+parent);
							parent.setOutlinePaint(Color.green);
						}
					}
				};
				
				Transition leaveLine = new LeaveOnTag(magnet){
					public void action(){
						if(!getShape().hasTag(oTag)){
							parent.setOutlinePaint(null);
							parent = null;
							System.out.println("LeaveOnShape: "+parent);
						}
					}
				};


				/* add drop square to line */
				Transition release = new Release(BUTTON1, ">> start") {
					public void action(){
						System.out.println("DraggedShape released");
						List<CShape> l = magnet.getFilledShapes();
						Iterator<CShape> it = l.iterator();
						CShape aux = null;
						/* list all of shapes tagged with 'magnet' */
						while(it.hasNext()){
							CShape s = it.next();
							/* take just lines with squares on */
							if(!s.hasTag(oTag)){
								System.out.println(s);

								if(draggedShape.contains(draggedShape.getMinX(), s.getCenterY()) != null){
									System.out.println("Parent found: " + s + "/" +draggedShape);
									aux = s;
									draggedShape.addTag(magnet);
									s.addChild(draggedShape);
									draggedShape.translateTo(p.getX(), s.getCenterY());
								}
								else if(draggedShape.contains(s.getCenterX(), draggedShape.getMinY()) != null){
									System.out.println("Parent found: " + s + "/" +draggedShape);	
									aux = s;
									draggedShape.addTag(magnet);
									s.addChild(draggedShape);
									draggedShape.translateTo(s.getCenterX(), p.getY());
								}
							}
						}

						if(aux == null && draggedShape.hasTag(magnet)){
							//							System.out.println(draggedShape.getParent());
							System.out.println("DraggedShape magnet removed.");
							draggedShape.removeTag(magnet);
							draggedShape.translateTo(p.getX(), p.getY());
						}
						if(aux == null && draggedShape.getParent() != null){
							draggedShape.getParent().removeChild(draggedShape);
							System.out.println("Parent removed: " + draggedShape.getParent());
							draggedShape.translateTo(p.getX(), p.getY());
						}
						p = null;
						aux = null;
					}
				} ;
			};

			public State DragHLine = new State(){
				Transition dragLine = new Drag(BUTTON1, ">> DragHLine"){
					public void action(){
						Point2D q = getPoint();
						draggedShape.translateBy(0, q.getY() - p.getY());
						p = q;
					}
				};
				Transition release = new Release(BUTTON1, ">> start") {
					public void action(){
						System.out.println("HLine released.");
						p = null;
					}
				} ;
			} ;

			public State DragVLine = new State(){
				Transition dragLine = new Drag(BUTTON1, ">> DragVLine"){
					public void action(){
						Point2D q = getPoint();
						draggedShape.translateBy(q.getX() - p.getX(), 0);
						p = q;
					}
				};
				Transition release = new Release(BUTTON1, ">> start") {
					public void action(){
						System.out.println("VLine released.");
						p = null;
					}
				} ;
			} ;


		} ;
		sm.attachTo(canvas);

		JFrame jsm = new JFrame();

		StateMachineVisualization smv = new StateMachineVisualization(sm);

		jsm.add(smv);

		jsm.getContentPane().add(smv) ;

		jsm.pack() ;

		jsm.setVisible(true) ;

		pack() ;
		setVisible(true) ;
		canvas.requestFocusInWindow() ;
	}

	public void populate() {
		int width = canvas.getWidth() ;
		int height = canvas.getHeight() ;

		double s = (Math.random()/2.0+0.5)*30.0 ;
		double x = s + Math.random()*(width-2*s) ;
		double y = s + Math.random()*(height-2*s) ;


		int red = (int)((0.8+Math.random()*0.2)*255) ;
		int green = (int)((0.8+Math.random()*0.2)*255) ;
		int blue = (int)((0.8+Math.random()*0.2)*255) ;

		CRectangle r = canvas.newRectangle(x,y,s,s) ;
		r.setFillPaint(new Color(red, green, blue)) ;
		r.addTag(oTag) ;
	}

	public static void main(String[] args) {
		MagneticGuides guides = new MagneticGuides("Magnetic guides",600,600) ;
		for (int i=0; i<20; ++i) guides.populate() ;
		guides.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
	}

}
