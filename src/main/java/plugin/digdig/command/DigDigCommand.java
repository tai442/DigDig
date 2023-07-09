package plugin.digdig.command;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import plugin.digdig.Main;
import plugin.digdig.PlayerScoreData;
import plugin.digdig.data.ExecutingPlayer;
import plugin.digdig.mapper.data.PlayerScore;

/**
 * 制限時間内に鉱石を採掘して、スコアを獲得するゲームを機動するコマンドです。
 * スコアは鉱石によって変わり、採掘した鉱石の合計によってスコアが変動します。
 * 結果はプレイヤー名、点数、日時などで保存されます。
 */

public class DigDigCommand extends BaseCommand implements org.bukkit.event.Listener {

  public static final String LIST = "list";

  public static final int GAME_TIME = 20;
  private BukkitRunnable gameTimer;

  private final Main main;
  private final PlayerScoreData playerScoreData = new PlayerScoreData();

  private final List<ExecutingPlayer> executingPlayerList = new ArrayList<>();
  private final List<BlockState> originalBlocks = new ArrayList<>();
  private final List<BlockState> changedBlocks = new ArrayList<>();
  private final Map<Player, Location> originalPlayerLocations = new HashMap<>();

  public DigDigCommand(Main main) {
    this.main = main;
  }

  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label, String[] args) {
    // 最初の引数が『List』だったらスコアを一覧表示して処理を終了する。
    if (args.length == 1 && LIST.equals(args[0])) {
      sendPlayerScoreList(player);
      return false;
    }

    ExecutingPlayer nowExecutingPlayer = getPlayerScore(player);
    nowExecutingPlayer.setGameTime(GAME_TIME);

    player.sendTitle("ゲーム開始！", GAME_TIME + "秒間で鉱石を掘って得点を稼ごう！" , 10, 50, 0);

    initPlayerStatus(player);

    replaceBlocks(player);

    startGameTimer(player);
    return true;
  }

  @Override
  public boolean onExecuteNPCCommand(CommandSender sender, Command command, String label, String[] args) {
    return false;
  }

  @EventHandler
  public void onBlockBreakEvent(BlockBreakEvent e) {
    Player player = e.getPlayer();
    Block block = e.getBlock();

    if (!changedBlocks.contains(block.getState())) {
      return;
    }
    executingPlayerList.stream()
        .filter(p -> p.getPlayerName().equals(player.getName()))
        .findFirst()
        .ifPresent(p -> {
          int point = 0;
          switch (block.getType()) {
            case AIR -> {
              return;
            }
            case COAL_ORE -> point = 3;
            case IRON_ORE -> point = 5;
            case COPPER_ORE, GOLD_ORE -> point = 10;
            case LAPIS_ORE, REDSTONE_ORE -> point = 30;
            case EMERALD_ORE, DIAMOND_ORE -> point = 50;
            default -> {
            }
          }
          p.setScore(p.getScore() + point);
          if (point == 0) {
            return;
          }
          player.sendMessage("ブロックを破壊！　現在のスコアは　" + p.getScore() + "点！");
        });
  }

  /**
   * 現在登録されているスコアの一覧をメッセージに送る。
   *
   * @param player プレイヤー
   */
  private void sendPlayerScoreList(Player player) {
    List<plugin.digdig.mapper.data.PlayerScore> playerScoreList = playerScoreData.selectList();
    for (plugin.digdig.mapper.data.PlayerScore playerScore : playerScoreList) {
      player.sendMessage(playerScore.getId() + " | "
          + playerScore.getPlayerName() + " | "
          + playerScore.getScore() + " | "
          + playerScore.getRegisteredDt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  /**
   * 現在実行しているプレイヤーのスコア情報を取得する。
   *
   * @param player コマンドを実行したプレイヤー
   * @return 現在実行しているプレイヤーのスコア情報
   */
  private ExecutingPlayer getPlayerScore(Player player) {
    ExecutingPlayer executingPlayer = new ExecutingPlayer(player.getName());
    if (executingPlayerList.isEmpty()) {
      executingPlayer = addNewPlayer(player);
      return executingPlayer;
    } else {
      executingPlayer = executingPlayerList.stream()
          .findFirst()
          .map(ps -> ps.getPlayerName().equals(player.getName())
              ? ps
              : addNewPlayer(player)).orElse(executingPlayer);
    }
    removePotionEffect(player);
    executingPlayer.setScore(0);
    return executingPlayer;
  }

  /**
   * 新規のプレイヤー情報をリストに追加します。
   *
   * @param player コマンドを実行したプレイヤー
   * @return 新規プレイヤー
   */
  private ExecutingPlayer addNewPlayer(Player player) {
    ExecutingPlayer newPlayer = new ExecutingPlayer(player.getName());
    executingPlayerList.add(newPlayer);
    return newPlayer;
  }

  /**
   * ゲームを始める前にプレイヤーの状態を設定する。体力と空腹度を最大にして、鉄のツルハシを持たせる。
   * プレイヤーのスタート位置を保存する。
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void initPlayerStatus(Player player) {
    player.setHealth(20);
    player.setFoodLevel(20);

    PlayerInventory inventory = player.getInventory();
    inventory.setItemInMainHand(new ItemStack(Material.IRON_PICKAXE));

    originalPlayerLocations.put(player, player.getLocation());
  }

  /**
   * ゲームを開始します。
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void startGameTimer(Player player) {
    gameTimer = new BukkitRunnable() {
      int timeLeft = 20;
      boolean blocksChanged = false; // ブロックの変更が行われたかを示すフラグ

      @Override
      public void run() {
        if (timeLeft > 0) {
          timeLeft--;
        } else {
          endGame(player);
          cancel();
        }

        if (timeLeft == 20 && !blocksChanged) {
          blocksChanged = true; // ブロックの変更が行われたことをフラグにセットする
        }
      }
    };
    gameTimer.runTaskTimer(main, 20, 20);
  }

  /**
   * ゲーム開始時にブロックを指定した鉱石に変更し、変更前後の状態をリストに保存する。
   *
   * @param world　ワールド
   * @param baseLocation　プレイヤーの立っている場所
   * @param radius　中心ブロックを含めた半径
   * @param yOffset　ブロックのY座標のオフセット値
   * @param blockTypes　鉱石の種類
   */
  private void changeBlocks(World world, Location baseLocation, int radius, int yOffset, Material... blockTypes) {
    int numBlockTypes = blockTypes.length;
    int index = 0;
    for (int x = -radius; x <= radius; x++) {
      for (int z = -radius; z <= radius; z++) {
        Location blockLocation = baseLocation.clone().add(x, yOffset, z);
        Block block = world.getBlockAt(blockLocation);

        originalBlocks.add(block.getState());

        Material currentBlockType = blockTypes[index];
        block.setType(currentBlockType);
        index = (index + 1) % numBlockTypes;

        changedBlocks.add(block.getState());
      }
    }
  }

  /**
   * プレイヤーを中心とした縦5マス×横5マス×高さ5マスの範囲のブロックを鉱石やその他のブロックに変更します。
   *
   * @param player　コマンドを実行したプレイヤー
   */
  private void replaceBlocks(Player player) {
    World world = player.getWorld();
    Location playerLocation = player.getLocation();
    int radius = 2; // 中心ブロックを含めた半径2の範囲（縦5マス×横5マス）

    changeBlocks(world, playerLocation, radius, -1,
        Material.STONE, Material.COAL_ORE);
    changeBlocks(world, playerLocation, radius, -2,
        Material.ACACIA_WOOD, Material.STONE, Material.IRON_ORE);
    changeBlocks(world, playerLocation, radius, -3,
        Material.ACACIA_WOOD, Material.OAK_WOOD, Material.STONE, Material.COPPER_ORE, Material.GOLD_ORE);
    changeBlocks(world, playerLocation, radius, -4,
        Material.ACACIA_WOOD, Material.OAK_WOOD, Material.BIRCH_WOOD, Material.STONE, Material.LAPIS_ORE, Material.REDSTONE_ORE);
    changeBlocks(world, playerLocation, radius, -5,
        Material.ACACIA_WOOD, Material.OAK_WOOD, Material.BIRCH_WOOD, Material.JUNGLE_WOOD, Material.STONE, Material.EMERALD_ORE, Material.DIAMOND_ORE);
  }

  /**
   * ゲームを終了します。合計スコアを時間経過後に表示します。
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void endGame(Player player) {
    executingPlayerList.forEach(nowExecutingPlayer -> {
      Optional.ofNullable(Bukkit.getPlayer(nowExecutingPlayer.getPlayerName()))
          .ifPresent(p -> {
            player.sendTitle(
                "ゲーム終了！",
                "あなたのスコア： " + nowExecutingPlayer.getScore() + "点！",
                10, 50, 0);

            removePotionEffect(player);
          });

      restorePlayerPositions(player);
      restoreOriginalBlocks();

      if (nowExecutingPlayer.getScore() >= 70) {
        World world = player.getWorld();
        spawnFirework(world, player);
      }

      playerScoreData.insert(new PlayerScore(
          nowExecutingPlayer.getPlayerName(),
          nowExecutingPlayer.getScore()));

      Optional.ofNullable(gameTimer)
          .ifPresent(BukkitRunnable::cancel);
    });
  }

  /**
   *
   * @param world ワールド
   * @param player　プレイヤー
   */
  private void  spawnFirework(World world, Player player) {
    List<Color> colorList = List.of(Color.RED, Color.BLUE, Color.WHITE, Color.GRAY);
      for (Color color : colorList) {
        Firework firework = world.spawn(player.getLocation(), Firework.class);

        FireworkMeta fireworkMeta = firework.getFireworkMeta();

        fireworkMeta.addEffect(
            FireworkEffect.builder()
                .withColor(color)
                .with(FireworkEffect.Type.BALL_LARGE)
                .withFlicker()
                .build());
        fireworkMeta.setPower(0);

        firework.setFireworkMeta(fireworkMeta);
      }
  }

  /**
   * プレイヤーに設定されている特殊効果を除外します。
   *
   * @param player　コマンドを実行したプレイヤー
   */
  private void removePotionEffect(Player player) {
    player.getActivePotionEffects().stream()
        .map(PotionEffect::getType)
        .forEach(player::removePotionEffect);
  }

  /**
   * ブロックをゲーム開始前の状態に戻します。
   */
  private void restoreOriginalBlocks() {
    for (BlockState blockState : originalBlocks) {
      blockState.update(true, false);
    }
    originalBlocks.clear();
  }

  /**
   * プレイヤーをゲーム開始前の位置に戻します。
   */
  private void restorePlayerPositions(Player player) {
    executingPlayerList.stream()
        .filter(p -> p.getPlayerName().equals(player.getName()))
        .findFirst()
        .ifPresent(p -> {
          Location originalLocation = originalPlayerLocations.get(player);
          if (originalLocation != null) {
            player.teleport(originalLocation);
          }
        });
  }
}