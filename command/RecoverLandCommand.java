//public class RecoverLandCommand implements Command {
//    private Player player;
//    private GamePanel gp;
//
//    public RecoverLandCommand(GamePanel gp, Player player) {
//        this.gp = gp;
//        this.player = player;
//    }
//
//    @Override
//    public void execute() {
//        int tileX = player.getTileX();
//        int tileY = player.getTileY();
//
//        Tile tile = gp.map.getTile(tileX, tileY);
//        if (tile.getLandState() != Tile.LandState.UNTOUCHED) {
//            tile.setLandState(Tile.LandState.UNTOUCHED);
//            tile.removeCrop();
//            gp.playSE(10); // suara recover berhasil
//            System.out.println("Land recovered at (" + tileX + ", " + tileY + ")");
//        } else {
//            System.out.println("Land already untouched.");
//        }
//    }
//}
