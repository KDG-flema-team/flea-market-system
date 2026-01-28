-- users
INSERT INTO users (name, email, password, role, enabled)
VALUES
  ('出品者A', 'sellerA@example.com', '{noop}password', 'USER', TRUE),
  ('購入者B', 'xyz@example.com', '{noop}password', 'USER', TRUE),
  ('運営者C', 'adminC@example.com',  '{noop}adminpass','ADMIN',TRUE);

-- category
INSERT INTO category (name) VALUES
  ('本'), ('家電'), ('ファッション'), ('おもちゃ'),('文房具');

-- item
INSERT INTO item (user_id, name, description, price, category_id, status)
VALUES
  ((SELECT id FROM users WHERE email='sellerA@example.com'),
   'Javaプログラミング入門','初心者向けのJava入門書です。',1500.00,
   (SELECT id FROM category WHERE name='本'),'出品中'),

  ((SELECT id FROM users WHERE email='sellerA@example.com'),
   'ワイヤレスイヤホン','ノイズキャンセリング機能付き。',8000.00,
   (SELECT id FROM category WHERE name='家電'),'出品中'),
   
  ((SELECT id FROM users WHERE email='sellerA@example.com'),
   '超面白い本','とても面白く大きい本',2000.00,
   (SELECT id FROM category WHERE name='本'),'出品中');

   
INSERT INTO app_order (item_id, buyer_id, price, status, created_at)
VALUES
(
    (SELECT id FROM item WHERE name='超面白い本'),
    (SELECT id FROM users WHERE email='xyz@example.com'),
    (SELECT price FROM item WHERE name='超面白い本'),
    '購入済',
    NOW()
);

-- notice (通知データ - 50件)
INSERT INTO notice (title, content, created_at, user_id, is_read) VALUES
-- 出品者Aへの通知（20件：購入通知、メッセージ、レビューなど）
('商品が購入されました', 'あなたの出品した「Javaプログラミング入門」が購入されました。', NOW() - INTERVAL '1 hour', (SELECT id FROM users WHERE email='sellerA@example.com'), FALSE),
('新しいメッセージ', '購入者から商品について問い合わせがあります。', NOW() - INTERVAL '2 hours', (SELECT id FROM users WHERE email='sellerA@example.com'), FALSE),
('商品が購入されました', '「ワイヤレスイヤホン」が購入されました。取引を進めてください。', NOW() - INTERVAL '5 hours', (SELECT id FROM users WHERE email='sellerA@example.com'), FALSE),
('レビューが投稿されました', 'あなたの商品に新しいレビューが投稿されました。★★★★★', NOW() - INTERVAL '6 hours', (SELECT id FROM users WHERE email='sellerA@example.com'), FALSE),
('お気に入り登録', 'あなたの商品がお気に入りに追加されました。', NOW() - INTERVAL '8 hours', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('取引完了', '「超面白い本」の取引が完了しました。', NOW() - INTERVAL '12 hours', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('新しいメッセージ', '取引相手からメッセージが届いています。', NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email='sellerA@example.com'), FALSE),
('値下げ交渉', '「Javaプログラミング入門」に値下げ交渉のリクエストが届いています。', NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email='sellerA@example.com'), FALSE),
('売上金振込完了', '今月の売上金10,000円が振り込まれました。', NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('ランクアップ！', 'おめでとうございます！シルバーランクに昇格しました。', NOW() - INTERVAL '3 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('商品閲覧数増加', 'あなたの商品の閲覧数が100回を超えました。', NOW() - INTERVAL '3 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('新しいフォロワー', '5人の新しいフォロワーがあなたをフォローしました。', NOW() - INTERVAL '4 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('商品が購入されました', '「文房具セット」が購入されました。', NOW() - INTERVAL '5 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('レビュー依頼', '最近の取引についてレビューをお願いします。', NOW() - INTERVAL '6 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('キャンペーン情報', '出品手数料50%オフキャンペーン実施中！', NOW() - INTERVAL '7 days', (SELECT id FROM users WHERE email='sellerA@example.com'), FALSE),
('システムメンテナンス', '1月30日午前2時よりメンテナンスを実施します。', NOW() - INTERVAL '8 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('お気に入り登録', 'あなたの商品が3件お気に入りに追加されました。', NOW() - INTERVAL '9 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('新しいメッセージ', '購入希望者から質問が届いています。', NOW() - INTERVAL '10 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('取引評価完了', '取引相手があなたを評価しました。★★★★☆', NOW() - INTERVAL '14 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),
('クーポン配布', '次回出品時に使える500円クーポンを獲得しました。', NOW() - INTERVAL '15 days', (SELECT id FROM users WHERE email='sellerA@example.com'), TRUE),

-- 購入者Bへの通知（20件：購入確認、発送、レビュー依頼など）
('購入ありがとうございます', 'ご購入いただいた「超面白い本」の決済が完了しました。', NOW() - INTERVAL '30 minutes', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('商品発送完了', '「超面白い本」が発送されました。配送状況を確認できます。', NOW() - INTERVAL '3 hours', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('新しいメッセージ', '出品者から発送に関するメッセージが届いています。', NOW() - INTERVAL '4 hours', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('配送予定通知', 'あなたの商品は明日到着予定です。', NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('受取確認のお願い', '商品到着後、受取確認をお願いします。', NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('レビュー依頼', '「超面白い本」の取引はいかがでしたか？レビューをお願いします。', NOW() - INTERVAL '3 days', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('ポイント付与', 'レビュー投稿で100ポイント獲得しました！', NOW() - INTERVAL '4 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('おすすめ商品', 'あなたにおすすめの本が3件あります。', NOW() - INTERVAL '5 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('セール開始', '家電カテゴリで最大30%オフセール開催中！', NOW() - INTERVAL '6 days', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('値下げ通知', 'お気に入りの商品が値下げされました。', NOW() - INTERVAL '7 days', (SELECT id FROM users WHERE email='xyz@example.com'), FALSE),
('新着商品', 'フォロー中の出品者が新商品を出品しました。', NOW() - INTERVAL '8 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('取引完了', '「Javaプログラミング入門」の取引が完了しました。', NOW() - INTERVAL '9 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('ウォッチリスト', 'ウォッチリストの商品が間もなく終了します。', NOW() - INTERVAL '10 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('購入履歴', '今月の購入金額が15,000円に達しました。', NOW() - INTERVAL '11 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('クーポン配布', '次回購入時に使える10%オフクーポンを獲得！', NOW() - INTERVAL '12 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('新しいメッセージ', '出品者から返信が届きました。', NOW() - INTERVAL '13 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('お気に入り商品再出品', 'お気に入りの商品が再出品されました。', NOW() - INTERVAL '14 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('ランクアップ間近', 'あと2回の購入でシルバーランクに昇格します。', NOW() - INTERVAL '16 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('キャンペーン情報', '期間限定！全品送料無料キャンペーン実施中。', NOW() - INTERVAL '18 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),
('アカウント情報更新', 'パスワード更新のお知らせ', NOW() - INTERVAL '20 days', (SELECT id FROM users WHERE email='xyz@example.com'), TRUE),

-- 運営者Cへの通知（10件：システム、管理関連）
('新規ユーザー登録', '本日の新規ユーザー登録数：25人', NOW() - INTERVAL '1 hour', (SELECT id FROM users WHERE email='adminC@example.com'), FALSE),
('通報受信', 'ユーザーからの通報が1件あります。確認してください。', NOW() - INTERVAL '3 hours', (SELECT id FROM users WHERE email='adminC@example.com'), FALSE),
('システムアラート', 'サーバー負荷が80%を超えています。', NOW() - INTERVAL '5 hours', (SELECT id FROM users WHERE email='adminC@example.com'), FALSE),
('売上レポート', '本日の売上：125,000円（前日比+15%）', NOW() - INTERVAL '1 day', (SELECT id FROM users WHERE email='adminC@example.com'), TRUE),
('ユーザー問い合わせ', '新しい問い合わせが8件あります。', NOW() - INTERVAL '2 days', (SELECT id FROM users WHERE email='adminC@example.com'), TRUE),
('バックアップ完了', 'データベースバックアップが正常に完了しました。', NOW() - INTERVAL '3 days', (SELECT id FROM users WHERE email='adminC@example.com'), TRUE),
('不正アクセス検知', '不審なログイン試行を検知しました。', NOW() - INTERVAL '4 days', (SELECT id FROM users WHERE email='adminC@example.com'), TRUE),
('月次レポート', '12月の取引件数：1,250件、総売上：3,500,000円', NOW() - INTERVAL '7 days', (SELECT id FROM users WHERE email='adminC@example.com'), TRUE),
('システム更新完了', 'バージョン2.5.0へのアップデートが完了しました。', NOW() - INTERVAL '10 days', (SELECT id FROM users WHERE email='adminC@example.com'), TRUE),
('管理者権限変更', '新しい管理者が追加されました。', NOW() - INTERVAL '15 days', (SELECT id FROM users WHERE email='adminC@example.com'), TRUE);