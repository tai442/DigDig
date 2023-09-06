package plugin.digdig.mapper.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ゲーム時間を扱うオブジェクト。
 * DBに存在するテーブルと連動する。
 */
@Getter
@Setter
@NoArgsConstructor
public class GameTime {
  private int id;
  private int gameTime;

  public GameTime(int gameTime) {
    this.gameTime = gameTime;
  }
}
