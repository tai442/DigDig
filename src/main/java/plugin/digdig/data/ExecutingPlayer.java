package plugin.digdig.data;

import lombok.Getter;
import lombok.Setter;

/**
 * DIgDigのゲームを実行する際のスコア情報を扱うオブジェクト。
 * プレイヤー名、合計点数、日時などの情報を持つ。
 */
@Getter
@Setter

public class ExecutingPlayer {
  private String playerName;
  private int score;
  private int gameTime;
  private Location originalLocation;

  public ExecutingPlayer() {
  }

  private class Location {

  }
}
