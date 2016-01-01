package tk.greydynamics.Game;

public class Point {

	public float x;
        public float y;
        public float z;
        public float width;
        public float height;
        public float depth;
        
        public float colorR;
        public float colorG;
        public float colorB;
        

        public Point(float x, float y, float z,float size, float colorR, float colorG, float colorB) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.height = size;
            this.depth = size;
            this.width = size;
            this.colorR = colorR;
            this.colorG = colorG;
            this.colorB = colorB;
        }


		public float getX() {
			return x;
		}


		public void setX(float x) {
			this.x = x;
		}


		public float getY() {
			return y;
		}


		public void setY(float y) {
			this.y = y;
		}


		public float getZ() {
			return z;
		}


		public void setZ(float z) {
			this.z = z;
		}


		public float getWidth() {
			return width;
		}


		public void setWidth(float width) {
			this.width = width;
		}


		public float getHeight() {
			return height;
		}


		public void setHeight(float height) {
			this.height = height;
		}


		public float getDepth() {
			return depth;
		}


		public void setDepth(float depth) {
			this.depth = depth;
		}


		public float getColorR() {
			return colorR;
		}


		public void setColorR(float colorR) {
			this.colorR = colorR;
		}


		public float getColorG() {
			return colorG;
		}


		public void setColorG(float colorG) {
			this.colorG = colorG;
		}


		public float getColorB() {
			return colorB;
		}


		public void setColorB(float colorB) {
			this.colorB = colorB;
		}
    }