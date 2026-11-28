/* =====================================================================
   MyWords browser screens (Role D, redesign 28 Nov 2026)
   All data and AI work happens in Java (POST /api/<action>). This file only
   draws screens, reads the microphone through the browser, and speaks aloud.

   Fastest paths for the communicator:
     picture -> "Use this"                  = 2 taps
     Tap to speak -> Done -> "Use this"     = 3 taps
     Yes / No / Call someone                = 1 tap
   ===================================================================== */
"use strict";

/* ------------------------------ words on screen ------------------------------ */
const T = {
  en: {
    tagline: "Let me finish my sentence.", tryDemo: "Try with demo data", logIn: "Log in", signUp: "Sign up",
    yourName: "Your name", voice: "Voice", male: "Male", female: "Female", email: "Email", password: "Password",
    grpTalk: "Talk", grpMine: "Mine", grpSettings: "Settings", grpAccount: "Account",
    navHome: "Home", navTalk: "Talk", navType: "Type keywords", navWrite: "Write my own sentence",
    navPhrases: "Saved sentences", navPeople: "People I talk to", navWords: "My quick words", navFeedback: "What MyWords learned",
    language: "Language", textSize: "Text size", voiceSpeed: "Voice speed", normal: "Normal", slow: "Slow",
    wording: "Wording style", dark: "Dark screen", aiSharing: "AI suggestions", switchAccount: "Use another account",
    disclaimer: "Student prototype. Not a medical device and not a diagnosis tool.",
    homeTitle: "What do you want to say?", tapToSpeak: "Tap to speak", orType: "Or type a few words",
    talkingTo: "Talking to", quickWords: "Or tap a picture", addPersonShort: "Add",
    listening: "Listening…", typeTitle: "Type a few words", listenHint: "Say a few words. Tap Done when you finish.",
    typeHint: "Short words are fine, like: kopi, Ah Ma, tomorrow", paused: "Paused. Tap the microphone to continue.",
    yourWords: "Your words", makeSentences: "Done, make sentences", cancel: "Cancel",
    chooseTitle: "Which one do you mean?", thinking: "Making sentences…", noneTryAgain: "None of these, try again",
    writeMyself: "Write it myself", useThis: "Use this", edit: "Edit", notRight: "Not right", useMine: "Use my version",
    ai: "AI suggestion", saved: "Saved sentence", listen: "Listen",
    whyNot: "What was wrong?", rWRONG_MEANING: "Wrong meaning", rWRONG_TONE: "Wrong tone", rWRONG_PERSON_CONTEXT: "Wrong person", rOTHER_UNSURE: "Not sure",
    correction: "Better sentence (optional)", saveFeedback: "Remember this to improve suggestions",
    reuseCorrection: "Let AI use my better sentence later", rejected: "Not right",
    sayAgain: "Say again", done: "Done", keepTitle: "Keep this sentence?",
    keepOnce: "Just this once", keepOnceSub: "Nothing is saved",
    keepSave: "Save to my sentences", keepSaveSub: "Only on this computer",
    keepLearn: "Save and help AI learn my way", keepLearnSub: "May be sent with future AI requests",
    fromSaved: "This is already one of your saved sentences.",
    writeTitle: "Write your own sentence", sayIt: "Say it",
    phrasesTitle: "Saved sentences", exportSelected: "Export ticked sentences", noPhrases: "No saved sentences for this person yet.",
    aiMayUse: "AI may use", aiNotUse: "AI won't use", peopleTitle: "People I talk to", addPerson: "Add a person",
    wordsTitle: "My quick words", wordsSub: "Your own picture buttons on the home screen.", addWord: "Add a quick word", noWords: "No quick words yet.",
    feedbackTitle: "What MyWords learned", noFeedback: "Nothing learned for this person yet.",
    yes: "Yes", no: "No", callHelp: "Call someone", stop: "Stop",
    sayYes: "Yes.", sayNo: "No.", callPhrase: "Excuse me, can someone come here please?",
    to: (n) => "To " + n, youSaid: (w, n) => "You said “" + w + "” · to " + n,
    titles: { home: "MyWords", listen: "Talk", choose: "Choose a sentence", say: "Speak", write: "Write", phrases: "Saved sentences", people: "People", words: "Quick words", feedback: "Learned" },
    cats: { Family: "Family", Friends: "Friends", Healthcare: "Healthcare", Others: "Others" },
    name: "Name", group: "Group", tone: "How should I sound?", callLine: "What to say when calling them (optional)",
    save: "Save", word: "Word or short phrase", saveDone: "Saved.", deleted: "Deleted.",
    confirmDelete: "Delete this?", confirmDeletePerson: "Delete this person and everything saved for them?",
    aiOnTitle: "Turn on AI suggestions?",
    aiOnBody: "When you ask for sentences, MyWords sends your keywords, wording style, the person's tone and group, and up to 3 saved sentences and 2 feedback records that you allowed, to the AI service set up by your team. AI replies that are shown are saved in data/ai_log.txt on this computer.",
    aiOffTitle: "Turn off AI suggestions?", aiOffBody: "MyWords will only use your saved sentences.",
    turnOn: "Turn on", turnOff: "Turn off", ok: "OK",
    voiceTitle: "Before you talk", voiceBody: "Your browser turns your voice into text. Chrome and Edge may use their own online speech service for this. Only the text is sent to MyWords on this computer. You can always type instead.",
    voiceUnsupported: "Voice input does not work in this browser. Please type, or use Chrome or Edge.",
    voiceBlocked: "The microphone is blocked. Allow it in the browser, or type instead.",
    voiceNothing: "I did not hear anything. Tap the microphone and try again.",
    noVoiceOut: "This browser cannot read aloud.", needWords: "Say or type a few words first.",
    needPerson: "Add a person to talk to first.", reportTitle: "Export these sentences?", download: "Download .txt",
    tickFirst: "Tick the sentences to export first.", javaOff: "Start the MyWords Java app, then open this page again.",
    langNames: { en: "English", ms: "Bahasa Melayu", zh: "中文" },
    tiles: [["💧", "Drink", "drink, water", "#3A9BDC"], ["🍚", "Eat", "hungry, eat", "#E59A2F"], ["🚽", "Toilet", "toilet", "#7B6FD6"], ["🤕", "Pain", "pain", "#E0564F"],
            ["🥶", "Cold", "cold, blanket", "#2FB3C0"], ["😴", "Rest", "tired, rest", "#9A7BD4"], ["💊", "Medicine", "medicine", "#D9578F"], ["🏠", "Go home", "want, go home", "#5E9E4F"]]
  },
  ms: {
    tagline: "Biar saya habiskan ayat saya.", tryDemo: "Cuba dengan data demo", logIn: "Log masuk", signUp: "Daftar",
    yourName: "Nama anda", voice: "Suara", male: "Lelaki", female: "Perempuan", email: "E-mel", password: "Kata laluan",
    grpTalk: "Bercakap", grpMine: "Milik saya", grpSettings: "Tetapan", grpAccount: "Akaun",
    navHome: "Utama", navTalk: "Bercakap", navType: "Taip kata kunci", navWrite: "Tulis ayat sendiri",
    navPhrases: "Ayat disimpan", navPeople: "Orang yang saya bercakap", navWords: "Kata pantas saya", navFeedback: "Apa yang MyWords belajar",
    language: "Bahasa", textSize: "Saiz tulisan", voiceSpeed: "Kelajuan suara", normal: "Biasa", slow: "Perlahan",
    wording: "Gaya ayat", dark: "Skrin gelap", aiSharing: "Cadangan AI", switchAccount: "Guna akaun lain",
    disclaimer: "Prototaip pelajar. Bukan alat perubatan atau diagnosis.",
    homeTitle: "Apa yang anda mahu cakap?", tapToSpeak: "Tekan untuk bercakap", orType: "Atau taip beberapa perkataan",
    talkingTo: "Bercakap dengan", quickWords: "Atau tekan gambar", addPersonShort: "Tambah",
    listening: "Mendengar…", typeTitle: "Taip beberapa perkataan", listenHint: "Sebut beberapa perkataan. Tekan Siap bila selesai.",
    typeHint: "Perkataan pendek pun boleh, contoh: kopi, Ah Ma, esok", paused: "Berhenti seketika. Tekan mikrofon untuk sambung.",
    yourWords: "Perkataan anda", makeSentences: "Siap, buat ayat", cancel: "Batal",
    chooseTitle: "Yang mana maksud anda?", thinking: "Sedang membuat ayat…", noneTryAgain: "Tiada yang betul, cuba lagi",
    writeMyself: "Tulis sendiri", useThis: "Guna ini", edit: "Ubah", notRight: "Tak betul", useMine: "Guna versi saya",
    ai: "Cadangan AI", saved: "Ayat disimpan", listen: "Dengar",
    whyNot: "Apa yang salah?", rWRONG_MEANING: "Maksud salah", rWRONG_TONE: "Nada salah", rWRONG_PERSON_CONTEXT: "Orang salah", rOTHER_UNSURE: "Tak pasti",
    correction: "Ayat yang lebih baik (pilihan)", saveFeedback: "Ingat ini untuk cadangan lebih baik",
    reuseCorrection: "Benarkan AI guna ayat saya nanti", rejected: "Tak betul",
    sayAgain: "Sebut lagi", done: "Siap", keepTitle: "Simpan ayat ini?",
    keepOnce: "Sekali ini sahaja", keepOnceSub: "Tiada yang disimpan",
    keepSave: "Simpan ke ayat saya", keepSaveSub: "Hanya di komputer ini",
    keepLearn: "Simpan dan bantu AI faham cara saya", keepLearnSub: "Mungkin dihantar bersama permintaan AI akan datang",
    fromSaved: "Ini sudah salah satu ayat yang anda simpan.",
    writeTitle: "Tulis ayat anda sendiri", sayIt: "Sebut",
    phrasesTitle: "Ayat disimpan", exportSelected: "Eksport ayat yang ditanda", noPhrases: "Belum ada ayat disimpan untuk orang ini.",
    aiMayUse: "AI boleh guna", aiNotUse: "AI tak guna", peopleTitle: "Orang yang saya bercakap", addPerson: "Tambah orang",
    wordsTitle: "Kata pantas saya", wordsSub: "Butang gambar anda sendiri di skrin utama.", addWord: "Tambah kata pantas", noWords: "Belum ada kata pantas.",
    feedbackTitle: "Apa yang MyWords belajar", noFeedback: "Belum ada apa-apa untuk orang ini.",
    yes: "Ya", no: "Tidak", callHelp: "Panggil orang", stop: "Berhenti",
    sayYes: "Ya.", sayNo: "Tidak.", callPhrase: "Maaf, boleh seseorang datang ke sini?",
    to: (n) => "Kepada " + n, youSaid: (w, n) => "Anda sebut “" + w + "” · kepada " + n,
    titles: { home: "MyWords", listen: "Bercakap", choose: "Pilih ayat", say: "Sebut", write: "Tulis", phrases: "Ayat disimpan", people: "Orang", words: "Kata pantas", feedback: "Dipelajari" },
    cats: { Family: "Keluarga", Friends: "Kawan", Healthcare: "Kesihatan", Others: "Lain-lain" },
    name: "Nama", group: "Kumpulan", tone: "Bagaimana saya patut berbunyi?", callLine: "Apa nak sebut bila panggil mereka (pilihan)",
    save: "Simpan", word: "Perkataan atau frasa pendek", saveDone: "Disimpan.", deleted: "Dipadam.",
    confirmDelete: "Padam ini?", confirmDeletePerson: "Padam orang ini dan semua yang disimpan untuk mereka?",
    aiOnTitle: "Hidupkan cadangan AI?",
    aiOnBody: "Apabila anda minta ayat, MyWords menghantar kata kunci, gaya ayat, nada dan kumpulan orang itu, serta sehingga 3 ayat disimpan dan 2 rekod maklum balas yang anda benarkan, kepada perkhidmatan AI yang ditetapkan oleh pasukan anda. Jawapan AI yang dipaparkan disimpan dalam data/ai_log.txt di komputer ini.",
    aiOffTitle: "Matikan cadangan AI?", aiOffBody: "MyWords hanya akan guna ayat yang anda simpan.",
    turnOn: "Hidupkan", turnOff: "Matikan", ok: "OK",
    voiceTitle: "Sebelum anda bercakap", voiceBody: "Pelayar anda menukar suara kepada teks. Chrome dan Edge mungkin guna perkhidmatan pertuturan dalam talian mereka. Hanya teks dihantar ke MyWords di komputer ini. Anda sentiasa boleh menaip.",
    voiceUnsupported: "Input suara tidak berfungsi di pelayar ini. Sila taip, atau guna Chrome atau Edge.",
    voiceBlocked: "Mikrofon disekat. Benarkan dalam pelayar, atau taip.",
    voiceNothing: "Saya tak dengar apa-apa. Tekan mikrofon dan cuba lagi.",
    noVoiceOut: "Pelayar ini tidak boleh membaca dengan kuat.", needWords: "Sebut atau taip beberapa perkataan dahulu.",
    needPerson: "Tambah orang untuk bercakap dahulu.", reportTitle: "Eksport ayat-ayat ini?", download: "Muat turun .txt",
    tickFirst: "Tandakan ayat untuk dieksport dahulu.", javaOff: "Jalankan aplikasi Java MyWords, kemudian buka halaman ini semula.",
    langNames: { en: "English", ms: "Bahasa Melayu", zh: "中文" },
    tiles: [["💧", "Minum", "minum, air", "#3A9BDC"], ["🍚", "Makan", "lapar, makan", "#E59A2F"], ["🚽", "Tandas", "tandas", "#7B6FD6"], ["🤕", "Sakit", "sakit", "#E0564F"],
            ["🥶", "Sejuk", "sejuk, selimut", "#2FB3C0"], ["😴", "Rehat", "penat, rehat", "#9A7BD4"], ["💊", "Ubat", "ubat", "#D9578F"], ["🏠", "Balik", "nak balik rumah", "#5E9E4F"]]
  },
  zh: {
    tagline: "让我把话说完。", tryDemo: "用示范资料试试", logIn: "登录", signUp: "注册",
    yourName: "你的名字", voice: "声音", male: "男声", female: "女声", email: "电邮", password: "密码",
    grpTalk: "说话", grpMine: "我的", grpSettings: "设置", grpAccount: "账号",
    navHome: "首页", navTalk: "说话", navType: "打关键词", navWrite: "自己写一句",
    navPhrases: "保存的句子", navPeople: "我常说话的人", navWords: "我的快捷词", navFeedback: "MyWords 学到的",
    language: "语言", textSize: "字体大小", voiceSpeed: "语速", normal: "正常", slow: "慢",
    wording: "说话风格", dark: "深色屏幕", aiSharing: "AI 建议", switchAccount: "换一个账号",
    disclaimer: "学生原型，不是医疗器材，也不做诊断。",
    homeTitle: "你想说什么？", tapToSpeak: "按一下说话", orType: "或者打几个字",
    talkingTo: "跟谁说", quickWords: "或者按一张图片", addPersonShort: "添加",
    listening: "正在听…", typeTitle: "打几个字", listenHint: "说几个字，说完按“好了”。",
    typeHint: "几个字就可以，例如：kopi、阿嬷、明天", paused: "暂停了，按麦克风继续。",
    yourWords: "你的话", makeSentences: "好了，帮我造句", cancel: "取消",
    chooseTitle: "你是想说哪一句？", thinking: "正在造句…", noneTryAgain: "都不对，再说一次",
    writeMyself: "我自己写", useThis: "就这句", edit: "改一下", notRight: "不对", useMine: "用我改的",
    ai: "AI 建议", saved: "保存的句子", listen: "听听看",
    whyNot: "哪里不对？", rWRONG_MEANING: "意思不对", rWRONG_TONE: "语气不对", rWRONG_PERSON_CONTEXT: "对象不对", rOTHER_UNSURE: "说不上来",
    correction: "更好的句子（可不填）", saveFeedback: "记住这次，下次建议更准",
    reuseCorrection: "以后让 AI 参考我写的句子", rejected: "不对",
    sayAgain: "再说一次", done: "好了", keepTitle: "要保存这句话吗？",
    keepOnce: "只用这一次", keepOnceSub: "什么都不保存",
    keepSave: "保存到我的句子", keepSaveSub: "只存在这台电脑",
    keepLearn: "保存并让 AI 学我的说法", keepLearnSub: "以后请求 AI 时可能会一起发送",
    fromSaved: "这句已经在你保存的句子里了。",
    writeTitle: "自己写一句话", sayIt: "说出来",
    phrasesTitle: "保存的句子", exportSelected: "导出打勾的句子", noPhrases: "还没有给这个人保存的句子。",
    aiMayUse: "AI 可参考", aiNotUse: "AI 不参考", peopleTitle: "我常说话的人", addPerson: "添加一个人",
    wordsTitle: "我的快捷词", wordsSub: "出现在首页的自定义图片按钮。", addWord: "添加快捷词", noWords: "还没有快捷词。",
    feedbackTitle: "MyWords 学到的", noFeedback: "还没有跟这个人有关的记录。",
    yes: "是", no: "不是", callHelp: "叫人来", stop: "停",
    sayYes: "是的。", sayNo: "不是。", callPhrase: "不好意思，可以请人过来一下吗？",
    to: (n) => "对 " + n + " 说", youSaid: (w, n) => "你说的：“" + w + "” · 对 " + n,
    titles: { home: "MyWords", listen: "说话", choose: "选一句", say: "说出来", write: "自己写", phrases: "保存的句子", people: "联系人", words: "快捷词", feedback: "学到的" },
    cats: { Family: "家人", Friends: "朋友", Healthcare: "医护", Others: "其他" },
    name: "名字", group: "类别", tone: "要用什么语气？", callLine: "叫他们时要说的话（可不填）",
    save: "保存", word: "一个词或短句", saveDone: "已保存。", deleted: "已删除。",
    confirmDelete: "要删除吗？", confirmDeletePerson: "删除这个人和为他保存的所有内容？",
    aiOnTitle: "开启 AI 建议？",
    aiOnBody: "你请求造句时，MyWords 会把关键词、说话风格、对方的语气和类别，以及你允许的最多 3 条保存句子和 2 条反馈，发送给你们团队设置的 AI 服务。显示出来的 AI 回复会保存在这台电脑的 data/ai_log.txt。",
    aiOffTitle: "关闭 AI 建议？", aiOffBody: "MyWords 只会使用你保存的句子。",
    turnOn: "开启", turnOff: "关闭", ok: "好",
    voiceTitle: "说话之前", voiceBody: "浏览器会把你的声音转成文字。Chrome 和 Edge 可能会用它们自己的在线语音服务来做这件事。只有文字会传给这台电脑上的 MyWords。你随时可以改用打字。",
    voiceUnsupported: "这个浏览器不能用语音输入。请打字，或改用 Chrome / Edge。",
    voiceBlocked: "麦克风被封锁了。请在浏览器允许，或改用打字。",
    voiceNothing: "没听到声音。按麦克风再试一次。",
    noVoiceOut: "这个浏览器不能朗读。", needWords: "请先说或打几个字。",
    needPerson: "请先添加一个说话对象。", reportTitle: "导出这些句子？", download: "下载 .txt",
    tickFirst: "请先勾选要导出的句子。", javaOff: "请先启动 MyWords 的 Java 程序，再重新打开这个页面。",
    langNames: { en: "English", ms: "Bahasa Melayu", zh: "中文" },
    tiles: [["💧", "喝水", "喝水", "#3A9BDC"], ["🍚", "吃饭", "饿，吃饭", "#E59A2F"], ["🚽", "厕所", "厕所", "#7B6FD6"], ["🤕", "疼痛", "痛", "#E0564F"],
            ["🥶", "冷", "冷，被子", "#2FB3C0"], ["😴", "休息", "累，休息", "#9A7BD4"], ["💊", "吃药", "药", "#D9578F"], ["🏠", "回家", "想回家", "#5E9E4F"]]
  }
};
const CATS = ["Family", "Friends", "Healthcare", "Others"];
const CAT_STYLE = { Family: ["🏠", "#E07A3F"], Friends: ["😊", "#3A86D8"], Healthcare: ["🩺", "#7B6FD6"], Others: ["🙂", "#6F7682"] };
const REASONS = ["WRONG_MEANING", "WRONG_TONE", "WRONG_PERSON_CONTEXT", "OTHER_UNSURE"];
const SPEECH_LANG = { en: "en-MY", ms: "ms-MY", zh: "cmn-Hans-CN" };

/* ------------------------------ app state ------------------------------ */
const state = {
  user: null,                 // the Java profile returned by /api/session
  lang: localStorage.getItem("mw_lang") || "en",
  partnerId: localStorage.getItem("mw_partner") || "",
  screen: "home", back: [], forward: [],
  keywords: "", request: "", suggestions: [], rejected: [],
  pending: null,              // the sentence on the Speak screen, not yet committed
  keep: "once",
  rate: Number(localStorage.getItem("mw_rate") || 0.92),
  typing: false, recognition: null, keepListening: false
};
const $ = (id) => document.getElementById(id);
const tr = () => T[state.lang] || T.en;
const icon = (name) => `<svg class="ico" aria-hidden="true"><use href="#i-${name}"/></svg>`;
const esc = (s) => String(s).replace(/[&<>"']/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));

/* ------------------------------ Java backend ------------------------------ */
async function api(action, fields = {}) {
  let response;
  try {
    response = await fetch("/api/" + action, { method: "POST", body: new URLSearchParams(fields) });
  } catch (e) {
    throw new Error(tr().javaOff);
  }
  const data = await response.json().catch(() => ({}));
  if (!response.ok) throw new Error(data.error || tr().javaOff);
  return data;
}

/* ------------------------------ small helpers ------------------------------ */
function toast(message, type = "") {
  const el = document.createElement("div");
  el.className = "toast " + type;
  el.textContent = message;
  $("toast").append(el);
  setTimeout(() => { el.style.opacity = "0"; setTimeout(() => el.remove(), 300); }, 2800);
}
const fail = (e) => toast(e.message || tr().javaOff, "error");

function people() {
  const list = [];
  if (!state.user) return list;
  for (const cat of CATS) for (const p of state.user.members[cat] || []) list.push({ ...p, cat });
  return list;
}
function partner() { return people().find((p) => p.id === state.partnerId) || null; }
function ensurePartner() {
  if (!partner()) state.partnerId = people()[0]?.id || "";
  localStorage.setItem("mw_partner", state.partnerId);
}
function boldKeywords(sentence) {
  let html = esc(sentence);
  const words = state.keywords.toLowerCase().split(/[\s,，。.!?、]+/).filter((w) => w.length >= (/[一-鿿]/.test(w) ? 1 : 3));
  for (const w of words) {
    const safe = esc(w).replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    const re = /[一-鿿]/.test(w) ? new RegExp(`(${safe})`, "g") : new RegExp(`\\b(${safe}\\w*)`, "gi");
    html = html.replace(re, "<b>$1</b>");
  }
  return html;
}

/* ------------------------------ dialogs ------------------------------ */
function dialog(html, wire) {
  const d = $("dialog");
  d.innerHTML = `<form method="dialog" class="dlg">${html}</form>`;
  d.returnValue = "";
  return new Promise((resolve) => {
    wire?.(d, resolve);
    d.onclose = () => resolve(d.returnValue || "");
    d.showModal();
  });
}
async function confirmBox(title, body, okLabel, okClass = "btn-go") {
  const r = await dialog(`<h2>${esc(title)}</h2>${body ? `<p class="t-sub">${esc(body)}</p>` : ""}
    <div class="actions"><button class="btn ${okClass}" value="ok">${esc(okLabel)}</button><button class="btn btn-soft" value="no">${esc(tr().cancel)}</button></div>`);
  return r === "ok";
}

/* ------------------------------ navigation (back / next) ------------------------------ */
const ORDER = { home: 0, listen: 1, choose: 2, say: 3, write: 2, phrases: 1, people: 1, words: 1, feedback: 1 };
function show(screen, forward) {
  const was = state.screen;
  if (was === "listen" && screen !== "listen") stopListening();
  if (was === "say" && screen !== "say") commitPending();
  state.screen = screen;
  document.querySelectorAll(".screen").forEach((s) => s.classList.toggle("active", s.dataset.screen === screen));
  const el = document.querySelector(`.screen[data-screen="${screen}"]`);
  el.classList.remove("enter-fwd", "enter-back");
  void el.offsetWidth;
  if (was !== screen) el.classList.add(forward ? "enter-fwd" : "enter-back");
  window.scrollTo(0, 0);
  closeDrawer();
  renderScreen(screen);
  syncNav();
}
function go(screen) {
  if (screen === state.screen) { renderScreen(screen); closeDrawer(); return; }
  state.back.push(state.screen);
  state.forward = [];
  show(screen, ORDER[screen] >= ORDER[state.screen]);
}
function goHomeFresh() {
  state.back = []; state.forward = [];
  show("home", false);
}
function navBack() {
  if (!state.back.length) return;
  const from = state.screen;
  if (from !== "say") state.forward.push(from); // a spoken sentence is finished, you cannot go "next" into it again
  show(state.back.pop(), false);
}
function navNext() {
  if (!state.forward.length) return;
  const to = state.forward.pop();
  if (to === "choose" && !state.suggestions.length) return syncNav();
  state.back.push(state.screen);
  show(to, true);
}
function syncNav() {
  $("nav-back").disabled = !state.back.length;
  $("nav-next").disabled = !state.forward.length;
  $("bar-title").textContent = tr().titles[state.screen] || "MyWords";
  document.querySelectorAll("#drawer [data-go]").forEach((b) => b.classList.toggle("current", b.dataset.go === state.screen));
  const p = partner();
  $("who-pill").hidden = !p;
  if (p) { $("who-av").textContent = CAT_STYLE[p.cat][0]; $("who-name").textContent = p.name; }
}
function openDrawer() { document.body.classList.add("drawer-open"); }
function closeDrawer() { document.body.classList.remove("drawer-open"); }

/* ------------------------------ texts & settings ------------------------------ */
function applyTexts() {
  document.documentElement.lang = state.lang === "zh" ? "zh-Hans" : state.lang;
  document.querySelectorAll("[data-t]").forEach((el) => {
    const v = tr()[el.dataset.t];
    if (typeof v === "string") el.textContent = v;
  });
  const signup = $("tab-signup").classList.contains("on");
  $("auth-submit").textContent = signup ? tr().signUp : tr().logIn;
}
function applyPrefs() {
  const u = state.user;
  document.documentElement.classList.toggle("dark", u?.theme === "dark");
  document.documentElement.dataset.font = u?.fontSize || "normal";
  document.querySelector('meta[name="theme-color"]').content = u?.theme === "dark" ? "#121315" : "#FFFFFF";
  const seg = (id, value) => $(id).querySelectorAll("button").forEach((b) => b.classList.toggle("on", b.dataset.v === String(value)));
  seg("set-lang", state.lang);
  seg("set-font", u?.fontSize || "normal");
  seg("set-gender", u?.gender || "Male");
  seg("set-speed", state.rate < 0.8 ? "0.7" : "0.92");
  $("set-dark").classList.toggle("on", u?.theme === "dark");
  $("set-ai").classList.toggle("on", !!u?.aiSharing);
  const style = $("set-style");
  if (u && ![...style.options].some((o) => o.value === u.style)) style.add(new Option(u.style, u.style));
  if (u) style.value = u.style;
}
let prefQueue = Promise.resolve();
function savePrefs(changes) {
  const run = prefQueue.then(async () => {
    const u = state.user;
    const fields = { style: u.style, tone: u.tone, gender: u.gender, language: state.lang, theme: u.theme, font: u.fontSize, ...changes };
    state.user = (await api("profile", fields)).user;
    applyPrefs();
  });
  prefQueue = run.catch(() => {});
  return run.catch(fail);
}
function setLanguage(lang) {
  state.lang = T[lang] ? lang : "en";
  localStorage.setItem("mw_lang", state.lang);
  applyTexts(); applyPrefs(); renderScreen(state.screen); syncNav();
  if (state.user) savePrefs({ language: state.lang });
}

/* ------------------------------ sign in ------------------------------ */
function showAuth() {
  $("app").hidden = true;
  $("auth").hidden = false;
  applyTexts();
}
async function enter(user) {
  state.user = user;
  if (T[user.language] && !localStorage.getItem("mw_lang")) state.lang = user.language;
  $("auth").hidden = true;
  $("app").hidden = false;
  ensurePartner();
  applyTexts(); applyPrefs();
  $("dr-av").textContent = (user.name || "?").charAt(0).toUpperCase();
  $("dr-name").textContent = user.name;
  $("dr-email").textContent = user.email;
  goHomeFresh();
}
async function boot() {
  applyTexts();
  try {
    const { user } = await api("session");
    if (user) return enter(user);
    // No login wall: the demo opens straight away unless this computer used a real account before.
    if (localStorage.getItem("mw_account") === "own") return showAuth();
    enter((await api("demo")).user);
  } catch (e) {
    showAuth();
    fail(e);
  }
}
function wireAuth() {
  let gender = "Male";
  const tab = (signup) => {
    $("tab-login").classList.toggle("on", !signup);
    $("tab-signup").classList.toggle("on", signup);
    $("f-name").hidden = !signup;
    $("f-gender").hidden = !signup;
    $("a-name").required = signup;
    applyTexts();
  };
  $("tab-login").onclick = () => tab(false);
  $("tab-signup").onclick = () => tab(true);
  $("f-gender").querySelectorAll("button").forEach((b) => b.onclick = () => {
    gender = b.dataset.gender;
    $("f-gender").querySelectorAll("button").forEach((x) => x.classList.toggle("on", x === b));
  });
  $("demo-btn").onclick = async () => {
    try { localStorage.removeItem("mw_account"); enter((await api("demo")).user); } catch (e) { fail(e); }
  };
  $("auth-form").onsubmit = async (event) => {
    event.preventDefault();
    const signup = $("tab-signup").classList.contains("on");
    try {
      const { user } = await api(signup ? "signup" : "login",
        { email: $("a-email").value, password: $("a-pass").value, name: $("a-name").value, gender });
      localStorage.setItem("mw_account", "own");
      $("auth-form").reset();
      enter(user);
    } catch (e) { fail(e); }
  };
}

/* ------------------------------ screens ------------------------------ */
function renderScreen(screen) {
  if (!state.user) return;
  const r = { home: renderHome, listen: renderListen, choose: renderChoose, say: renderSay, write: renderWrite,
    phrases: renderPhrases, people: renderPeople, words: renderWords, feedback: renderFeedback }[screen];
  r?.();
}

/* HOME */
function renderHome() {
  const row = $("people-row");
  row.innerHTML = "";
  for (const p of people()) {
    const b = document.createElement("button");
    b.type = "button";
    b.className = "person" + (p.id === state.partnerId ? " on" : "");
    b.style.setProperty("--c", CAT_STYLE[p.cat][1]);
    b.innerHTML = `<span class="av">${CAT_STYLE[p.cat][0]}</span><span>${esc(p.name)}<small>${esc(tr().cats[p.cat])}</small></span>`;
    b.onclick = () => { state.partnerId = p.id; localStorage.setItem("mw_partner", p.id); clearSuggestions(); renderHome(); syncNav(); };
    row.append(b);
  }
  const add = document.createElement("button");
  add.type = "button"; add.className = "person add";
  add.innerHTML = `<span class="av">${icon("plus")}</span><span>${esc(tr().addPersonShort)}</span>`;
  add.onclick = addPersonDialog;
  row.append(add);
  row.querySelector(".on")?.scrollIntoView({ block: "nearest", inline: "nearest" });

  const board = $("board");
  board.innerHTML = "";
  const tiles = tr().tiles.map(([e, label, words, c]) => ({ e, label, words, c }));
  for (const cat of Object.keys(state.user.customNeeds || {})) {
    for (const need of state.user.customNeeds[cat]) tiles.push({ e: "⭐", label: need.label, words: need.label, c: "#C9A227", mine: true });
  }
  for (const tile of tiles) {
    const b = document.createElement("button");
    b.type = "button";
    b.className = "tile" + (tile.mine ? " mine" : "");
    b.style.setProperty("--c", tile.c);
    b.innerHTML = `<span class="e" aria-hidden="true">${tile.e}</span><span>${esc(tile.label)}</span>`;
    b.onclick = () => { b.classList.add("picked"); setTimeout(() => b.classList.remove("picked"), 300); suggest(tile.words); };
    board.append(b);
  }
}

/* LISTEN / TYPE */
function renderListen() {
  const p = partner();
  $("listen-title").textContent = state.typing ? tr().typeTitle : tr().listening;
  $("listen-to").textContent = p ? tr().to(p.name) : "";
  $("listen-ring").hidden = state.typing;
  $("words").placeholder = state.typing ? tr().typeHint : tr().listenHint;
  $("listen-status").textContent = state.typing ? "" : (state.recognition ? tr().listenHint : tr().paused);
}
async function startTalk() {
  if (!partner()) return toast(tr().needPerson, "error");
  state.typing = false;
  $("words").value = "";
  go("listen");
  const Rec = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!Rec) { toast(tr().voiceUnsupported, "error"); return startType(true); }
  if (!localStorage.getItem("mw_voice_ok")) {
    if (!await confirmBox(tr().voiceTitle, tr().voiceBody, tr().ok)) return startType(true);
    localStorage.setItem("mw_voice_ok", "1");
  }
  startListening();
}
function startType(stay) {
  if (!partner()) return toast(tr().needPerson, "error");
  stopListening();
  state.typing = true;
  if (!stay) { $("words").value = ""; go("listen"); } else renderListen();
  setTimeout(() => $("words").focus(), 60);
}
function startListening() {
  const Rec = window.SpeechRecognition || window.webkitSpeechRecognition;
  stopSpeaking();
  const rec = new Rec();
  rec.lang = SPEECH_LANG[state.lang] || "en-MY";
  rec.continuous = true;
  rec.interimResults = true;
  let finalText = $("words").value.trim();
  rec.onresult = (event) => {
    let interim = "";
    for (let i = event.resultIndex; i < event.results.length; i++) {
      const chunk = event.results[i][0].transcript.trim();
      if (event.results[i].isFinal) finalText = (finalText + (finalText && state.lang !== "zh" ? " " : "") + chunk).trim();
      else interim += chunk;
    }
    $("words").value = (finalText + (interim ? (finalText && state.lang !== "zh" ? " " : "") + interim : "")).trim();
  };
  rec.onerror = (event) => {
    if (event.error === "no-speech" || event.error === "aborted") return;
    state.keepListening = false;
    toast(event.error === "not-allowed" || event.error === "service-not-allowed" ? tr().voiceBlocked : tr().voiceNothing, "error");
  };
  rec.onend = () => {
    if (state.keepListening && state.recognition === rec) { try { rec.start(); return; } catch (e) { /* fall through */ } }
    if (state.recognition === rec) state.recognition = null;
    document.body.classList.remove("recording");
    if (state.screen === "listen") renderListen();
  };
  state.recognition = rec;
  state.keepListening = true;
  document.body.classList.add("recording");
  try { rec.start(); } catch (e) { stopListening(); toast(tr().voiceUnsupported, "error"); }
  renderListen();
}
function stopListening() {
  state.keepListening = false;
  const rec = state.recognition;
  state.recognition = null;
  document.body.classList.remove("recording");
  if (rec) { try { rec.stop(); } catch (e) { /* already stopped */ } }
}
function finishWords() {
  const wasListening = !!state.recognition;
  stopListening();
  // the last spoken words can arrive just after stopping
  setTimeout(() => {
    const words = $("words").value.trim();
    if (!words) return toast(tr().needWords, "error");
    suggest(words);
  }, wasListening ? 450 : 0);
}

/* CHOOSE */
function clearSuggestions() { state.request = ""; state.suggestions = []; state.rejected = []; }
async function suggest(keywords) {
  const p = partner();
  if (!p) return toast(tr().needPerson, "error");
  state.keywords = keywords.slice(0, 300);
  clearSuggestions();
  state.message = "";
  state.loading = true;
  if (state.screen !== "choose") go("choose"); else renderChoose();
  const ticket = Symbol("request");
  state.ticket = ticket;
  try {
    const result = await api("suggest", { partner: p.id, keywords: state.keywords });
    if (state.ticket !== ticket) return;
    state.request = result.request;
    state.suggestions = result.suggestions;
    state.rejected = result.suggestions.map(() => null);
    state.message = result.message || "";
  } catch (e) {
    if (state.ticket !== ticket) return;
    state.message = e.message;
  }
  state.loading = false;
  if (state.screen === "choose") renderChoose();
}
function renderChoose() {
  const p = partner();
  $("choose-context").textContent = p ? tr().youSaid(state.keywords, p.name) : "";
  $("choose-loading").hidden = !state.loading;
  $("choose-message").hidden = !state.message || state.loading;
  $("choose-message").textContent = state.message || "";
  const cards = $("cards");
  cards.innerHTML = "";
  if (state.loading) return;
  state.suggestions.forEach((s, i) => cards.append(card(s, i)));
}
function card(s, index) {
  const el = document.createElement("article");
  const rejected = state.rejected[index];
  el.className = "card" + (rejected ? " rejected" : "");
  el.style.animationDelay = index * 70 + "ms";
  if (rejected) {
    el.innerHTML = `<p class="src">${esc(tr().rejected)} · ${esc(tr()["r" + rejected])}</p><p class="say-text">${esc(s.text)}</p>`;
    return el;
  }
  const source = s.source === "AI" ? `<span class="src ai">${esc(tr().ai)}</span>` : `<span class="src saved">${esc(tr().saved)}</span>`;
  el.innerHTML = `<div class="card-top">${source}<button class="icon-btn listen-btn" type="button" aria-label="${esc(tr().listen)}">${icon("volume")}</button></div>
    <p class="say-text">${boldKeywords(s.text)}</p>
    <div class="card-actions">
      <button class="btn btn-go use" type="button">${icon("check")}<span>${esc(tr().useThis)}</span></button>
      <button class="btn btn-soft edit" type="button">${icon("edit")}<span>${esc(tr().edit)}</span></button>
      <button class="btn btn-soft reject" type="button">${icon("x")}<span>${esc(tr().notRight)}</span></button>
    </div>`;
  el.querySelector(".listen-btn").onclick = (e) => speak(s.text, el);
  el.querySelector(".use").onclick = () => useSentence(index, s.text, false);
  el.querySelector(".edit").onclick = () => editCard(el, s, index);
  el.querySelector(".reject").onclick = () => rejectCard(el, s, index);
  return el;
}
function editCard(el, s, index) {
  el.innerHTML = `<textarea class="area" maxlength="500">${esc(s.text)}</textarea>
    <div class="card-actions" style="grid-template-columns:1fr auto"><button class="btn btn-go" type="button">${icon("check")}<span>${esc(tr().useMine)}</span></button>
    <button class="btn btn-soft" type="button">${esc(tr().cancel)}</button></div>`;
  const area = el.querySelector("textarea");
  area.focus();
  area.setSelectionRange(area.value.length, area.value.length);
  const [use, cancel] = el.querySelectorAll("button");
  use.onclick = () => { const text = area.value.trim(); if (text) useSentence(index, text, text !== s.text); };
  cancel.onclick = () => el.replaceWith(card(s, index));
}
function rejectCard(el, s, index) {
  el.innerHTML = `<p class="say-text">${esc(s.text)}</p><h3 class="t-title">${esc(tr().whyNot)}</h3>
    <div class="reasons">${REASONS.map((r) => `<button class="btn" type="button" data-r="${r}">${esc(tr()["r" + r])}</button>`).join("")}</div>
    <input class="input" maxlength="500" placeholder="${esc(tr().correction)}" />
    <label class="check"><input type="checkbox" class="keep-fb" />${esc(tr().saveFeedback)}</label>
    <label class="check"><input type="checkbox" class="reuse-fb" disabled />${esc(tr().reuseCorrection)}</label>
    <button class="btn btn-soft" type="button" data-cancel>${esc(tr().cancel)}</button>`;
  const correction = el.querySelector("input.input");
  const keepFb = el.querySelector(".keep-fb");
  const reuseFb = el.querySelector(".reuse-fb");
  const sync = () => { reuseFb.disabled = !keepFb.checked || !correction.value.trim(); if (reuseFb.disabled) reuseFb.checked = false; };
  correction.oninput = sync; keepFb.onchange = sync;
  el.querySelector("[data-cancel]").onclick = () => el.replaceWith(card(s, index));
  el.querySelectorAll("[data-r]").forEach((b) => b.onclick = async () => {
    try {
      await api("decision", { request: state.request, index, decision: "REJECT", reason: b.dataset.r,
        text: correction.value.trim(), saveFeedback: keepFb.checked, reuseFeedback: reuseFb.checked });
      state.rejected[index] = b.dataset.r;
      el.replaceWith(card(s, index));
      if (keepFb.checked) toast(tr().saveDone);
    } catch (e) { fail(e); }
  });
}
function useSentence(index, text, edited) {
  const s = state.suggestions[index];
  state.pending = { kind: "decision", index, text, decision: edited ? "EDIT" : "APPROVE", duplicate: s.source === "SAVED" && !edited };
  state.keep = "once";
  go("say");
  speak(text, $("stage"));
}

/* SAY */
function renderSay() {
  const p = partner();
  const pending = state.pending;
  $("say-to").textContent = p ? tr().to(p.name) : "";
  $("say-text").textContent = pending ? pending.text : "";
  const dup = !pending || pending.duplicate;
  $("keep-opts").hidden = dup;
  $("keep").querySelector("h2").textContent = dup ? tr().fromSaved : tr().keepTitle;
  $("keep").hidden = !pending;
  document.querySelectorAll(".keep-opt").forEach((b) => b.classList.toggle("on", b.dataset.keep === state.keep));
}
async function commitPending() {
  const pending = state.pending;
  if (!pending) return;
  state.pending = null;
  const keep = state.keep;
  const p = partner();
  try {
    if (pending.kind === "decision") {
      const save = !pending.duplicate && keep !== "once";
      const learn = save && keep === "learn";
      await api("decision", { request: state.request, index: pending.index, decision: pending.decision, text: pending.text,
        savePhrase: save, reusePhrase: learn, saveFeedback: learn, reuseFeedback: learn });
      clearSuggestions();
      if (save) toast(tr().saveDone);
    } else if (keep !== "once" && p) {
      await api("manual", { partner: p.id, text: pending.text, keywords: (state.keywords || pending.text).slice(0, 300), reuse: keep === "learn" });
      toast(tr().saveDone);
    }
  } catch (e) { fail(e); }
}

/* WRITE (manual sentence, no AI) */
function renderWrite() {
  const p = partner();
  $("write-to").textContent = p ? tr().to(p.name) : "";
}
function writeGo() {
  const text = $("write-text").value.trim();
  if (!text) return toast(tr().needWords, "error");
  if (!partner()) return toast(tr().needPerson, "error");
  state.pending = { kind: "manual", text, duplicate: false };
  state.keep = "once";
  $("write-text").value = "";
  go("say");
  speak(text, $("stage"));
}

/* SAVED SENTENCES */
async function renderPhrases() {
  const p = partner();
  $("phrases-to").textContent = p ? tr().to(p.name) : "";
  const list = $("phrase-list");
  list.innerHTML = "";
  if (!p) return;
  try {
    const { phrases } = await api("phrases", { partner: p.id });
    phrases.sort((a, b) => (b.createdAt || 0) - (a.createdAt || 0));
    if (!phrases.length) list.innerHTML = `<p class="empty">${esc(tr().noPhrases)}</p>`;
    for (const ph of phrases) {
      const row = document.createElement("div");
      row.className = "row wrap";
      row.innerHTML = `<label class="check"><input type="checkbox" value="${esc(ph.id)}" aria-label="Export" /></label>
        <div class="grow"><p>${esc(ph.text)}</p></div>
        <div class="row-tools"><button class="chip-toggle${ph.reuse ? " on" : ""}" type="button">${esc(ph.reuse ? tr().aiMayUse : tr().aiNotUse)}</button>
        <button class="icon-btn" type="button" aria-label="${esc(tr().listen)}">${icon("volume")}</button>
        <button class="icon-btn" type="button" aria-label="Delete">${icon("delete")}</button></div>`;
      const [reuse, play, del] = row.querySelectorAll("button");
      reuse.onclick = async () => { try { await api("phrase-reuse", { partner: p.id, phrase: ph.id, reuse: !ph.reuse }); renderPhrases(); } catch (e) { fail(e); } };
      play.onclick = () => speak(ph.text, row);
      del.onclick = async () => {
        if (!await confirmBox(tr().confirmDelete, ph.text, tr().confirmDelete.replace("?", ""), "btn-stop")) return;
        try { await api("phrase-delete", { partner: p.id, phrase: ph.id }); toast(tr().deleted); renderPhrases(); } catch (e) { fail(e); }
      };
      list.append(row);
    }
    $("export-btn").hidden = !phrases.length;
  } catch (e) { fail(e); }
}
async function exportPhrases() {
  const p = partner();
  const ids = [...$("phrase-list").querySelectorAll("input:checked")].map((i) => i.value);
  if (!ids.length) return toast(tr().tickFirst, "error");
  try {
    const { report } = await api("report", { partner: p.id, selected: ids.join(",") });
    const ok = await dialog(`<h2>${esc(tr().reportTitle)}</h2><pre>${esc(report)}</pre>
      <div class="actions"><button class="btn btn-go" value="ok">${icon("download")}<span>${esc(tr().download)}</span></button><button class="btn btn-soft" value="no">${esc(tr().cancel)}</button></div>`);
    if (ok !== "ok") return;
    const url = URL.createObjectURL(new Blob([report], { type: "text/plain;charset=UTF-8" }));
    const a = document.createElement("a");
    a.href = url; a.download = "mywords-report.txt"; a.click();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  } catch (e) { fail(e); }
}

/* PEOPLE */
function renderPeople() {
  const box = $("people-list");
  box.innerHTML = "";
  for (const cat of CATS) {
    const members = state.user.members[cat] || [];
    if (!members.length) continue;
    box.insertAdjacentHTML("beforeend", `<h2 class="group-title">${CAT_STYLE[cat][0]} ${esc(tr().cats[cat])}</h2>`);
    const list = document.createElement("div");
    list.className = "list";
    for (const m of members) {
      const row = document.createElement("div");
      row.className = "row";
      row.innerHTML = `<div class="grow"><p>${esc(m.name)}</p><p class="row-sub">${esc(m.tone)}${m.customPhrase ? " · “" + esc(m.customPhrase) + "”" : ""}</p></div>
        <button class="btn btn-sm btn-go" type="button">${esc(tr().talkingTo)}</button>
        <button class="icon-btn" type="button" aria-label="Delete">${icon("delete")}</button>`;
      const [pick, del] = row.querySelectorAll("button");
      pick.onclick = () => { state.partnerId = m.id; localStorage.setItem("mw_partner", m.id); clearSuggestions(); goHomeFresh(); };
      del.onclick = async () => {
        if (!await confirmBox(tr().confirmDeletePerson, m.name, tr().confirmDelete.replace("?", ""), "btn-stop")) return;
        try { state.user = (await api("partner-delete", { partner: m.id })).user; ensurePartner(); renderPeople(); syncNav(); toast(tr().deleted); } catch (e) { fail(e); }
      };
      list.append(row);
    }
    box.append(list);
  }
}
async function addPersonDialog() {
  let cat = "Family";
  const r = await dialog(`<h2>${esc(tr().addPerson)}</h2>
    <label class="field"><span>${esc(tr().name)}</span><input class="input" name="name" maxlength="60" required /></label>
    <div class="field"><span>${esc(tr().group)}</span><div class="seg" id="cat-seg">${CATS.map((c) => `<button type="button" data-c="${c}" class="${c === cat ? "on" : ""}">${CAT_STYLE[c][0]} ${esc(tr().cats[c])}</button>`).join("")}</div></div>
    <label class="field"><span>${esc(tr().tone)}</span><input class="input" name="tone" maxlength="120" placeholder="Warm, polite, casual…" /></label>
    <label class="field"><span>${esc(tr().callLine)}</span><input class="input" name="call" maxlength="300" /></label>
    <div class="actions"><button class="btn btn-go" value="ok">${esc(tr().save)}</button><button class="btn btn-soft" value="no" formnovalidate>${esc(tr().cancel)}</button></div>`,
  (d) => d.querySelectorAll("#cat-seg button").forEach((b) => b.onclick = () => {
    cat = b.dataset.c;
    d.querySelectorAll("#cat-seg button").forEach((x) => x.classList.toggle("on", x === b));
  }));
  if (r !== "ok") return;
  const f = $("dialog").querySelector("form").elements;
  try {
    const result = await api("partner-add", { category: cat, name: f.namedItem("name").value, role: tr().cats[cat],
      tone: f.namedItem("tone").value, callPhrase: f.namedItem("call").value });
    state.user = result.user;
    state.partnerId = result.partnerId;
    localStorage.setItem("mw_partner", state.partnerId);
    toast(tr().saveDone);
    renderScreen(state.screen); syncNav();
  } catch (e) { fail(e); }
}

/* QUICK WORDS (custom needs stored by Java) */
function renderWords() {
  const list = $("word-list");
  list.innerHTML = "";
  let count = 0;
  for (const topic of Object.keys(state.user.customNeeds || {})) {
    for (const need of state.user.customNeeds[topic]) {
      count++;
      const row = document.createElement("div");
      row.className = "row";
      row.innerHTML = `<div class="grow"><p>⭐ ${esc(need.label)}</p></div><button class="icon-btn" type="button" aria-label="Delete">${icon("delete")}</button>`;
      row.querySelector("button").onclick = async () => {
        try { state.user = (await api("need-delete", { topic, need: need.id })).user; renderWords(); toast(tr().deleted); } catch (e) { fail(e); }
      };
      list.append(row);
    }
  }
  if (!count) list.innerHTML = `<p class="empty">${esc(tr().noWords)}</p>`;
}
async function addWordDialog() {
  const r = await dialog(`<h2>${esc(tr().addWord)}</h2>
    <label class="field"><span>${esc(tr().word)}</span><input class="input" name="label" maxlength="100" required placeholder="pasar malam, teh tarik…" /></label>
    <div class="actions"><button class="btn btn-go" value="ok">${esc(tr().save)}</button><button class="btn btn-soft" value="no" formnovalidate>${esc(tr().cancel)}</button></div>`);
  if (r !== "ok") return;
  try {
    state.user = (await api("need-add", { topic: "Others", label: $("dialog").querySelector("form").elements.namedItem("label").value })).user;
    toast(tr().saveDone);
    renderScreen(state.screen);
  } catch (e) { fail(e); }
}

/* FEEDBACK */
async function renderFeedback() {
  const p = partner();
  $("feedback-to").textContent = p ? tr().to(p.name) : "";
  const list = $("feedback-list");
  list.innerHTML = "";
  if (!p) return;
  try {
    const { feedback } = await api("feedback", { partner: p.id });
    if (!feedback.length) list.innerHTML = `<p class="empty">${esc(tr().noFeedback)}</p>`;
    for (const f of feedback) {
      const row = document.createElement("div");
      row.className = "row wrap";
      const reason = f.decision === "REJECT" ? tr().rejected + " · " + (tr()["r" + f.reason] || f.reason) : tr().useThis;
      const wording = f.confirmed && f.confirmed !== f.text ? `${esc(f.text)} → <b>${esc(f.confirmed)}</b>` : esc(f.text);
      const canReuse = f.decision !== "REJECT" || f.confirmed;
      row.innerHTML = `<div class="grow"><p>${wording}</p><p class="row-sub">${esc(reason)}</p></div>
        <div class="row-tools">${canReuse ? `<button class="chip-toggle${f.reuse ? " on" : ""}" type="button">${esc(f.reuse ? tr().aiMayUse : tr().aiNotUse)}</button>` : ""}
        <button class="icon-btn" type="button" aria-label="Delete">${icon("delete")}</button></div>`;
      const buttons = row.querySelectorAll("button");
      if (canReuse) buttons[0].onclick = async () => { try { await api("feedback-reuse", { partner: p.id, feedback: f.id, reuse: !f.reuse }); renderFeedback(); } catch (e) { fail(e); } };
      buttons[buttons.length - 1].onclick = async () => {
        try { await api("feedback-delete", { partner: p.id, feedback: f.id }); toast(tr().deleted); renderFeedback(); } catch (e) { fail(e); }
      };
      list.append(row);
    }
  } catch (e) { fail(e); }
}

/* ------------------------------ speaking aloud ------------------------------ */
let speakingEl = null;
function stopSpeaking() {
  if ("speechSynthesis" in window) speechSynthesis.cancel();
  speakingEl?.classList.remove("speaking");
  speakingEl = null;
  $("stop-voice").hidden = true;
}
function pickVoice() {
  const voices = speechSynthesis.getVoices();
  const want = { en: ["en-MY", "en-SG", "en-GB", "en"], ms: ["ms-MY", "ms", "id-ID"], zh: ["zh-CN", "zh-SG", "zh-TW", "zh"] }[state.lang] || ["en"];
  const female = state.user?.gender === "Female";
  const looksFemale = (v) => /female|woman|zira|hazel|susan|huihui|yaoyao|xiaoxiao|yasmin|karen|samantha|tingting/i.test(v.name);
  for (const code of want) {
    const matches = voices.filter((v) => v.lang.toLowerCase().startsWith(code.toLowerCase()));
    const local = matches.filter((v) => v.localService);
    const pool = local.length ? local : matches;
    if (pool.length) return pool.find((v) => looksFemale(v) === female) || pool[0];
  }
  return null;
}
function speak(text, el) {
  if (!("speechSynthesis" in window)) return toast(tr().noVoiceOut, "error");
  stopListening();
  stopSpeaking();
  const u = new SpeechSynthesisUtterance(text);
  const voice = pickVoice();
  if (voice) { u.voice = voice; u.lang = voice.lang; } else u.lang = SPEECH_LANG[state.lang];
  u.rate = state.rate;
  u.onend = u.onerror = () => { if (speakingEl === el) stopSpeaking(); };
  speakingEl = el || null;
  el?.classList.add("speaking");
  $("stop-voice").hidden = false;
  speechSynthesis.speak(u);
}
function chime() {
  try {
    const ctx = new (window.AudioContext || window.webkitAudioContext)();
    [[880, 0], [660, 0.32]].forEach(([freq, at]) => {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.frequency.value = freq;
      gain.gain.setValueAtTime(0.0001, ctx.currentTime + at);
      gain.gain.exponentialRampToValueAtTime(0.35, ctx.currentTime + at + 0.02);
      gain.gain.exponentialRampToValueAtTime(0.0001, ctx.currentTime + at + 0.9);
      osc.connect(gain).connect(ctx.destination);
      osc.start(ctx.currentTime + at);
      osc.stop(ctx.currentTime + at + 1);
    });
    setTimeout(() => ctx.close(), 1600);
  } catch (e) { /* sound is optional */ }
}
function callSomeone() {
  const btn = $("call-btn");
  btn.classList.add("ringing");
  setTimeout(() => btn.classList.remove("ringing"), 2600);
  chime();
  const p = partner();
  setTimeout(() => speak(p?.customPhrase || tr().callPhrase, btn), 700);
}

/* ------------------------------ wiring ------------------------------ */
function wire() {
  wireAuth();
  $("menu-btn").onclick = openDrawer;
  $("drawer-close").onclick = closeDrawer;
  $("scrim").onclick = closeDrawer;
  $("nav-back").onclick = navBack;
  $("nav-next").onclick = navNext;
  $("who-pill").onclick = () => go("people");
  document.querySelectorAll("[data-go]").forEach((b) => b.onclick = () => (b.dataset.go === "home" ? goHomeFresh() : go(b.dataset.go)));
  document.querySelectorAll("[data-action]").forEach((b) => b.onclick = () => {
    closeDrawer();
    ({ talk: startTalk, type: () => startType(false), "add-person": addPersonDialog, "add-word": addWordDialog })[b.dataset.action]();
  });
  $("talk-btn").onclick = startTalk;
  $("listen-ring").onclick = () => (state.recognition ? stopListening() : startTalk());
  $("done-btn").onclick = finishWords;
  $("cancel-btn").onclick = () => { stopListening(); $("words").value = ""; navBack(); };
  $("none-btn").onclick = () => { clearSuggestions(); state.typing = true; $("words").value = state.keywords; go("listen"); };
  $("say-again").onclick = () => speak(state.pending?.text || $("say-text").textContent, $("stage"));
  $("say-done").onclick = () => { stopSpeaking(); goHomeFresh(); };
  document.querySelectorAll(".keep-opt").forEach((b) => b.onclick = () => { state.keep = b.dataset.keep; renderSay(); });
  $("write-go").onclick = writeGo;
  $("export-btn").onclick = exportPhrases;
  $("yes-btn").onclick = (e) => speak(tr().sayYes, e.currentTarget);
  $("no-btn").onclick = (e) => speak(tr().sayNo, e.currentTarget);
  $("call-btn").onclick = callSomeone;
  $("stop-voice").onclick = stopSpeaking;

  $("set-lang").querySelectorAll("button").forEach((b) => b.onclick = () => setLanguage(b.dataset.v));
  $("set-font").querySelectorAll("button").forEach((b) => b.onclick = () => savePrefs({ font: b.dataset.v }));
  $("set-gender").querySelectorAll("button").forEach((b) => b.onclick = () => savePrefs({ gender: b.dataset.v }));
  $("set-speed").querySelectorAll("button").forEach((b) => b.onclick = () => { state.rate = Number(b.dataset.v); localStorage.setItem("mw_rate", b.dataset.v); applyPrefs(); });
  $("set-style").onchange = (e) => savePrefs({ style: e.target.value });
  $("set-dark").onclick = () => savePrefs({ theme: state.user.theme === "dark" ? "light" : "dark" });
  $("set-ai").onclick = async () => {
    const on = !state.user.aiSharing;
    if (!await confirmBox(on ? tr().aiOnTitle : tr().aiOffTitle, on ? tr().aiOnBody : tr().aiOffBody, on ? tr().turnOn : tr().turnOff)) return;
    try { state.user = (await api("consent", { enabled: on })).user; applyPrefs(); } catch (e) { fail(e); }
  };
  $("logout-btn").onclick = async () => {
    try {
      stopSpeaking(); stopListening();
      await api("logout");
      state.user = null;
      localStorage.setItem("mw_account", "own");
      closeDrawer();
      showAuth();
    } catch (e) { fail(e); }
  };
  document.addEventListener("keydown", (e) => { if (e.key === "Escape") { closeDrawer(); stopSpeaking(); } });
  if ("speechSynthesis" in window) speechSynthesis.getVoices();
}

wire();
boot();
