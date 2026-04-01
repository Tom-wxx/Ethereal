/**
 * Ethereal 社交 App - 全流程端到端测试
 * 覆盖：注册 → 完善资料 → 探索 → 互相喜欢 → 匹配 → 发送消息
 *
 * 运行：node test/e2e-flow.mjs
 */

const BASE = 'http://localhost:8080';
// 每次用不同手机号避免 60s 频率限制（11位，符合 ^1[3-9]\d{9}$）
const suffix = Date.now().toString().slice(-8);
const PHONE_A = `151${suffix}`;
const PHONE_B = `152${suffix}`;
console.log(`测试手机号: A=${PHONE_A}  B=${PHONE_B}`);

let tokenA, tokenB, userIdA, userIdB, convId;
let passed = 0, failed = 0;

// ─── 工具函数 ────────────────────────────────────────────────
async function req(method, path, body, token) {
  const res = await fetch(`${BASE}${path}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  return res.json();
}

function assert(label, condition, detail = '') {
  if (condition) {
    console.log(`  ✓ ${label}`);
    passed++;
  } else {
    console.error(`  ✗ ${label}${detail ? ' → ' + detail : ''}`);
    failed++;
  }
}

function section(title) {
  console.log(`\n━━━ ${title} ━━━`);
}

// ─── 测试步骤 ────────────────────────────────────────────────

section('1. 发送验证码');
{
  const r1 = await req('POST', '/api/auth/sms-code', { phone: PHONE_A });
  assert('用户A 发送验证码', r1.code === 200, JSON.stringify(r1));

  const r2 = await req('POST', '/api/auth/sms-code', { phone: PHONE_B });
  assert('用户B 发送验证码', r2.code === 200, JSON.stringify(r2));
}

section('2. 获取验证码（Dev接口）');
{
  const r1 = await req('GET', `/api/auth/dev/sms-code/${PHONE_A}`);
  assert('获取用户A 验证码', r1.code === 200 && r1.data, JSON.stringify(r1));
  const codeA = r1.data;

  const r2 = await req('GET', `/api/auth/dev/sms-code/${PHONE_B}`);
  assert('获取用户B 验证码', r2.code === 200 && r2.data, JSON.stringify(r2));
  const codeB = r2.data;

  section('3. 登录 / 自动注册');

  const loginA = await req('POST', '/api/auth/login', { phone: PHONE_A, code: codeA });
  assert('用户A 登录成功', loginA.code === 200 && loginA.data?.token, JSON.stringify(loginA));
  tokenA = loginA.data?.token;
  userIdA = loginA.data?.userId;

  const loginB = await req('POST', '/api/auth/login', { phone: PHONE_B, code: codeB });
  assert('用户B 登录成功', loginB.code === 200 && loginB.data?.token, JSON.stringify(loginB));
  tokenB = loginB.data?.token;
  userIdB = loginB.data?.userId;

  console.log(`  → 用户A id=${userIdA}  用户B id=${userIdB}`);
}

section('4. 完善个人资料');
{
  const r = await req('PUT', '/api/user/profile', {
    nickname: '测试用户Alpha',
    bio: '这是一个自动化测试账号',
    profession: '测试工程师',
    city: '上海',
    gender: 1,
    birthday: '1998-05-20',
  }, tokenA);
  assert('用户A 更新资料', r.code === 200, JSON.stringify(r));

  const profile = await req('GET', '/api/user/profile', null, tokenA);
  assert('用户A 读取资料', profile.code === 200 && profile.data, JSON.stringify(profile));
}

section('5. 设置位置');
{
  const r = await req('PUT', '/api/user/location', {
    longitude: 121.4737,
    latitude: 31.2304,
  }, tokenA);
  assert('用户A 更新位置', r.code === 200, JSON.stringify(r));
}

section('6. 探索 - 获取推荐卡片');
{
  const r = await req('GET', '/api/discovery/cards', null, tokenA);
  assert('获取推荐卡片列表', r.code === 200 && Array.isArray(r.data), JSON.stringify(r));
  console.log(`  → 返回 ${r.data?.length ?? 0} 张卡片`);
}

section('7. 用户A 喜欢用户B');
{
  const r = await req('POST', '/api/discovery/action', {
    targetUserId: userIdB,
    action: 'like',
  }, tokenA);
  assert('用户A 喜欢用户B', r.code === 200, JSON.stringify(r));
  console.log(`  → 是否立即匹配: ${r.data?.matched ?? false}`);
}

section('8. 用户B 喜欢用户A（触发互相匹配）');
{
  // 用户B 需要重新获取验证码登录（tokenB 已存在，复用）
  const r = await req('POST', '/api/discovery/action', {
    targetUserId: userIdA,
    action: 'like',
  }, tokenB);
  assert('用户B 喜欢用户A', r.code === 200, JSON.stringify(r));
  console.log(`  → 互相匹配触发: ${r.data?.matched ?? false}`);
}

section('9. 查看互相喜欢列表');
{
  const r = await req('GET', '/api/likes/mutual?page=1&size=10', null, tokenA);
  assert('用户A 查看互相喜欢', r.code === 200, JSON.stringify(r));
  console.log(`  → 匹配数: ${r.data?.total ?? 0}`);
}

section('10. 创建/获取会话');
{
  const createConv = await req('POST', '/api/chat/conversations', { targetUserId: userIdB }, tokenA);
  assert('创建与用户B 的会话', createConv.code === 200 && createConv.data?.id, JSON.stringify(createConv));
  convId = createConv.data?.id;
  console.log(`  → 会话 id=${convId}`);
}

section('11. 发送消息');
if (convId) {
  const r = await req('POST', '/api/chat/messages', {
    conversationId: convId,
    content: '你好！这是一条自动化测试消息',
    msgType: 1,
  }, tokenA);
  assert('用户A 发送消息', r.code === 200 && r.data?.id, JSON.stringify(r));

  const msgs = await req('GET', `/api/chat/messages/${convId}?size=10`, null, tokenA);
  assert('获取消息列表', msgs.code === 200 && Array.isArray(msgs.data), JSON.stringify(msgs));
  console.log(`  → 消息数: ${msgs.data?.length ?? 0}`);
} else {
  console.log('  ⚠ 无会话ID，跳过消息测试');
  failed++;
}

section('12. 发布动态');
{
  const r = await req('POST', '/api/moments', {
    content: '这是一条自动化测试动态，来自用户Alpha',
    longitude: 121.4737,
    latitude: 31.2304,
    locationName: '上海',
  }, tokenA);
  assert('用户A 发布动态', r.code === 200, JSON.stringify(r));

  const momentId = r.data?.id;
  if (momentId) {
    const like = await req('POST', `/api/moments/${momentId}/like`, null, tokenB);
    assert('用户B 点赞动态', like.code === 200, JSON.stringify(like));

    const comment = await req('POST', `/api/moments/${momentId}/comments`, {
      content: '测试评论',
    }, tokenB);
    assert('用户B 评论动态', comment.code === 200, JSON.stringify(comment));
  }
}

section('13. 清理 - 退出登录');
{
  const r1 = await req('POST', '/api/auth/logout', null, tokenA);
  assert('用户A 退出登录', r1.code === 200, JSON.stringify(r1));

  const r2 = await req('POST', '/api/auth/logout', null, tokenB);
  assert('用户B 退出登录', r2.code === 200, JSON.stringify(r2));
}

// ─── 汇总 ────────────────────────────────────────────────────
console.log(`\n${'═'.repeat(40)}`);
console.log(`测试结果: ${passed} 通过 / ${failed} 失败 / ${passed + failed} 总计`);
if (failed === 0) {
  console.log('🎉 全流程测试通过！');
} else {
  console.log('❌ 存在失败项，请检查上方日志');
  process.exit(1);
}
