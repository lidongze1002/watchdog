# 功能详解 · Feature Guide

> 盯梢 / Watchdog · v0.3.0
> 截图见 `docs/screenshots/`。

---

## 中文

### 1. 监控列表（01-monitor-list.png）

- 渐变 Hero：监控数量、「N 个有更新」、右上角语言设置、**「查看全部变化 ›」**入口。
- 搜索：按监控名称或网址实时过滤。
- 卡片：首字母头像、名称、URL、`每 1 小时 · 上次检查 11-15 13:03`。
- 状态胶囊：`有更新`（未读变化）或 `失败`（最近一次抓取失败）。
- 失败时附带一行红色原因，例如「连接超时，稍后再试」。
- 左滑删除会弹确认框；下拉刷新；空态有动画与引导。

### 2. 添加/编辑监控（02-add-monitor.png）

- 名称、URL（必须 http/https）。
- 关注点：逗号分隔关键词；留空表示页面任何变化都提醒。
- **检查频率**：15 分钟 / 1 小时 / 6 小时 / 每天（每个监控独立）。
- 保存后立即执行一次检查，建立基准快照。
- 说明文案提示系统调度可能有几分钟误差。

### 3. 监控详情（03-monitor-detail.png）

- URL、关注点、上次检查时间。
- **检查频率 + 计划下次时间**（按上次检查 + 间隔估算）。
- 最近一次失败时显示红色错误条（时间 + 人话原因）。
- 「立即检查」手动触发，不受频率限制。
- 最近快照：标题 + 正文前 200 字。
- 变化历史：时间、命中关键词、变化摘要；右上角「查看 Diff」。
- 删除监控（级联删除快照与变化记录）。

### 4. 变更对比（04-diff.png）

- 逐句对比上一份与当前快照：
  - 红色 `-` 删除句
  - 主色 `+` 新增句
- 关键词在文本中**黄色高亮**（大小写不敏感）。
- 没有差异时提示这是基准快照。

### 5. 全部变化（05-all-changes.png）

- 跨监控聚合最近 300 条变化。
- 按「今天 / 昨天 / 11月15日」分组，每组显示条数。
- 每条：监控名、时间、命中关键词胶囊、两句变化摘要。
- 搜索：监控名、关键词、变化内容。
- 点击条目跳转到该监控详情。

### 6. 语言切换（06-language.png）

- 中文 / English / 跟随系统，立即生效并持久化。
- 通知标题、渠道名、错误信息、日期分组同步本地化。

### 7. 通知策略

- 单条变化：标题为监控名，正文为「检测到变化：关键词」。
- 多个变化：合并为「N 个监控有变化」，BigText 列出每个监控与命中词。
- 点击通知回到 App；通知权限在首次启动时申请。

### 8. 失败处理

| 情况 | 行为 |
|---|---|
| HTTP 非 2xx | 记录 `网站返回 HTTP xxx` |
| 域名解析失败 | `域名无法解析，请检查网址或网络` |
| 连接超时/被拒绝 | `连接超时，稍后再试` / `无法连接服务器（网址失效或被拒绝）` |
| 证书错误 | `HTTPS 证书错误，暂时无法抓取` |
| 无可提取正文 | `页面无可提取正文（可能为 JS 渲染或空页）` |

失败时仍更新「上次检查时间」，避免每 15 分钟重复打同一个坏地址；成功后自动清除错误。

### 9. 调度

- WorkManager 周期任务固定 15 分钟（系统下限），每次只处理到期的监控。
- 判断逻辑集中在 `CheckSchedule.isDue`，有单元测试覆盖（未检查/未到期/到期/长间隔/最小 15 分钟/停用）。
- 手动「立即检查」绕过频率。

### 10. 数据模型

- `Monitor`：URL、名称、关键词、启用、创建时间、上次检查、上次快照、频率、最近错误与时间。
- `Snapshot`：抓取时间、标题、正文哈希、正文、是否有变化。
- `ChangeEvent`：快照、发生时间、命中关键词、摘要、是否已读。
- 数据库版本 2，迁移新增频率与错误字段。

### 11. 正文提取

- Jsoup 解析，移除 `script/style/noscript/nav/header/footer`。
- 优先 `article` 或 `main`，否则取 `body` 文本。
- 文本空白归一化后计算哈希，用于快速判断是否变化。

### 12. 测试

- 单元测试：关键词匹配、逐句 Diff、正文提取、调度到期判断、错误文案映射。
- 手动验证：频率设置、失败状态展示、Diff、全部变化分组、中英切换、通知合并。

### 13. 已知限制

- 不执行 JS；SPA 页面可能抓不到内容
- 不支持登录态、Cookie、验证码
- 周期任务最小 15 分钟，Doze 下可能延迟
- 无云同步/多设备

---

## English

### 1. Monitor list

Gradient hero with counts, language settings, and an "all changes" entry; debounced search; cards with avatar, name, URL, `Hourly · last check …`, and status chips (`Updated` / `Failed`) plus a one-line failure reason; swipe-to-delete with confirmation.

### 2. Add / edit monitor

Name, URL (http/https only), keyword list (empty = any change), and per-monitor interval (15 min / hourly / 6 h / daily). Saving runs an immediate baseline check.

### 3. Monitor detail

URL, keywords, last check, frequency and estimated next check, red failure banner with a human-readable reason, manual "check now", latest snapshot preview, change history with "view diff", and confirmed deletion.

### 4. Diff

Sentence-level comparison with red removals, primary-colored additions, and case-insensitive yellow keyword highlighting.

### 5. All changes

Up to 300 recent change events across monitors, grouped by day, each with monitor name, time, keyword chips and a snippet; searchable by monitor, keyword or content; tap to open the monitor.

### 6. Language

Chinese / English / system, applied instantly and persisted; notifications, channel names, errors and date groups are localized.

### 7. Notifications

Single change → titled by monitor; multiple changes in one run → one merged summary with per-monitor keyword lines. Tapping opens the app.

### 8. Failure handling

HTTP errors, DNS failures, timeouts, refused connections, TLS errors and empty extraction all map to friendly Chinese/English messages, are stored on the monitor, and are cleared on the next success. Last-check time still updates to avoid hammering a broken URL.

### 9. Scheduling

A 15-minute periodic WorkManager job filters monitors with `CheckSchedule.isDue`; manual checks bypass the interval. Unit tests cover never-checked, not-due, due, long intervals, the 15-minute floor and disabled monitors.

### 10. Data model

`Monitor` (URL, name, keywords, enabled, timestamps, last snapshot, interval, last error), `Snapshot` (time, title, hash, text, changed), `ChangeEvent` (snapshot, time, matched keywords, snippet, seen). Room schema v2.

### 11. Extraction

Jsoup strips scripts/nav and prefers `article`/`main`; normalized text is hashed for cheap change detection.

### 12. Testing

Unit tests for keyword matching, diff, extraction, scheduling and error mapping; manual verification of intervals, failures, diff, grouping, language and merged notifications.

### 13. Limitations

No JS execution or authenticated pages; 15-minute scheduling floor; local only.
