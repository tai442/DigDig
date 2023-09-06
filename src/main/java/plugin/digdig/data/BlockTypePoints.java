package plugin.digdig.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import plugin.digdig.OreScoreData;
import plugin.digdig.mapper.data.OreScore;

public class BlockTypePoints {
  private static final OreScoreData oreScoreData = new OreScoreData();
  private static final Map<String, Integer> oreTypeToPoints = new HashMap<>();
  private static final Map<Material, String> blockType = new HashMap<>();

  // 鉱石の名前と得点をデータベースから取得してマップに格納する
  static {
    List<OreScore> oreScores = oreScoreData.getAllOreScores();
    for (OreScore oreScore : oreScores) {
      oreTypeToPoints.put(oreScore.getOreType(), oreScore.getScore());
    }

    // 鉱石の種類と名前のマッピングはそのまま使用
    blockType.put(Material.COAL_ORE, "石炭の鉱石");
    blockType.put(Material.IRON_ORE, "鉄の鉱石");
    blockType.put(Material.COPPER_ORE, "銅の鉱石");
    blockType.put(Material.GOLD_ORE, "金の鉱石");
    blockType.put(Material.LAPIS_ORE, "ラピスラズリの鉱石");
    blockType.put(Material.REDSTONE_ORE, "レッドストーンの鉱石");
    blockType.put(Material.EMERALD_ORE, "エメラルドの鉱石");
    blockType.put(Material.DIAMOND_ORE, "ダイアモンドの鉱石");
  }

  public static int getPoints(Material material) {
    String oreType = material.toString();
    return oreTypeToPoints.getOrDefault(oreType, 0);
  }

  public static String getBlockType(Material material) {
    return blockType.getOrDefault(material, "");
  }
}

