import java.awt.geom.Point2D;

public class P extends Point2D.Double {
        public String name; 
        public P(double x, double y, String name)
        {
            super(x,y);
            this.name = name;       
        }   
        public P(double x, double y)
        {
            this(x,y,x+"_"+y);  
        }
         
        public String show()
        {
            int i = (int)(Math.round(x));
            int j = (int)(Math.round(y));               
            return name+" ["+i+" , "+j+"]";       
        }   
         
        @Override
        public String toString()
        {
            return "("+x+" , "+y+")";
        }
         
        @Override
        public boolean equals(Object o)
        {
            P p = (P)o;
            return this.name.equals(p.name);
        }
    }