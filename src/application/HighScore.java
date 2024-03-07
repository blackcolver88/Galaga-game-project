package application;

public record HighScore(int score) {
	
	@Override
	public String toString() {
	   return Integer.toString(this.score);
	}
	
}