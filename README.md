# JetpackLearning

更新日：Mar 19, 2025

Jetpack Compose と Room を使って作った Android の TODO アプリです。
機能の実装そのものと同じくらい、開発の進め方（ブランチ運用・Pull Request・コードレビュー）を実践することを目的にしています。


## 何ができるか

- セクション（横方向のリスト）で TODO を分類する
- TODO の追加・編集・削除、チェック状態の切り替え
- タグと数量の保持
- 削除は物理削除ではなく論理削除（`isDeleted` フラグ）
- データは端末内の SQLite に保存され、アプリを再起動しても残る

## 技術スタック

| 項目 | 内容 |
|---|---|
| 言語 | Kotlin |
| UI | Jetpack Compose（Material 3） |
| DB | Room |
| 非同期 | Kotlin Coroutines / Flow |
| compileSdk / targetSdk | 35 |
| minSdk | 24 |

## データ構造

2つのエンティティを持ち、`sectionNum` で TODO をセクションに紐付けています。

```kotlin
@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "sectionNum") val sectionNum: Int,   // 所属するセクション
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "onChecked") val isChecked: Boolean, // カラム名はプロパティ名と異なる
    @ColumnInfo(name = "tag") val tag: String,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "isDeleted") val isDeleted: Boolean  // 論理削除フラグ
)

@Entity(tableName = "todoSection")
data class TodoSection(
    @PrimaryKey(autoGenerate = true) val todoSectionNum: Int = 0,
    @ColumnInfo(name = "todoSectionTitle") val todoSectionTitle: String,
    @ColumnInfo(name = "isTodoDeleted") val todoOnDeleted: Boolean  // 同上
)
```

`isChecked` と `todoOnDeleted` は、プロパティ名とカラム名が一致していません。
`@ColumnInfo` で対応付けていますが、揃えたほうが読みやすい箇所です。

DAO は取得系を `Flow` で返しており、DB の変更が UI に自動で反映されます。

```kotlin
@Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY id ASC")
fun getActiveTodos(): Flow<List<Todo>>

@Query("UPDATE todo SET isDeleted = 1 WHERE id = :todoId")
suspend fun markAsDeleted(todoId: Int)
```

論理削除は、以前に作った [inventManagementApp](https://github.com/nemuinug/inventManagementApp) の
レビューで指摘を受けて採用した方式を、このリポジトリでも踏襲しています。
取得時は `WHERE isDeleted = 0` で除外し、削除操作は `UPDATE` で行っています。

## 開発の進め方

このリポジトリの主目的です。

### ブランチ運用

`main` / `develop` / `feature` の3層に分け、機能ごとに `develop` から
`feature/resolve-issue-<課題番号>` を切って、Pull Request 経由で `develop` に統合しています。

### Issue による課題管理

領域ごとに Issue を立て、そこからブランチを切る形で進めました。

| Issue | 領域 |
|---|---|
| #2 | デザイン |
| #3 | データベース |
| #4 | ネットワーク |
| #5 | バックグラウンド処理 |
| #6 | セキュリティ・認証 |
| #7 | 最適化 |
| #8 | ユーザーエクスペリエンス |

### コードレビュー

経験者の方に Pull Request のレビューをお願いし、指摘に対応しました。
主な指摘と、それに対して行った修正は次のとおりです。

| 指摘 | 対応 |
|---|---|
| クラス名の先頭は大文字のキャメルケースにする | 命名を修正 |
| ファイル名をクラス名に合わせる | ファイルを分割・改名 |
| Composable の中身が大きくなっている | 責務ごとに Composable を分割 |
| `LazyColumn` は要素数が可変の場合に使うもの | 固定要素の箇所を通常の `Column` に変更 |
| `selectedItem!!` のような強制アンラップは原則禁止 | null 安全な書き方に修正 |
| テーブルを変更するたびに DB のバージョンが上がる | マイグレーションの扱いを見直し |
| 扱うテーブルがクラス名から分かるので `getAll` でよい | DAO のメソッド名を簡潔化 |

指摘の量が一度に対応しきれる量ではなかったため、指摘ごとにコミットを分けて段階的に修正しました
（`コメントに対応` 〜 `コメントに対応5`）。

Pull Request と、そこでのやり取りは以下から確認できます。

- [Pull Requests](../../pulls?q=is%3Apr)

## ビルドと実行

```
Android Studio で開く → Gradle Sync → 実機またはエミュレータで Run
```

必要環境: Android Studio（Jetpack Compose 対応版）、JDK 17、Android SDK 35

## 現状と今後

- Pull Request #14（検索・メニュー・設定ボタン、スワイプ削除）は作業中です
- ネットワーク（Issue #4）以降の領域は未着手です

学習を目的に始めたリポジトリですが、レビューを受けて直す過程そのものが一番の収穫でした。
自分では動くコードを書いたつもりでも、他者から見ると読みにくい、あるいは将来壊れる書き方になっている箇所が多くあることを、具体的な指摘として受け取れました。
