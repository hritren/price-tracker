package price.tracker.bot;

public enum Strategy {

   BULL("bull"), BEAR("bear");

   private final String conviction;

   Strategy(String conviction) {
       this.conviction = conviction;
   }
}
