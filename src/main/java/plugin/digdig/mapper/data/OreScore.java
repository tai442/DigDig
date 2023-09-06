package plugin.digdig.mapper.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * プレイヤーのスコア情報を扱うオブジェクト。
 * DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class OreScore {
  private int id;
  private String oreType;
  private int score;

  public OreScore(String oreType, int score) {
    this.oreType = oreType;
    this.score = score;
  }
}
