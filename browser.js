/* Role D's screens use the same-origin Java app for account and conversation data. */
const browserText = {
  en: {
    feedback: "Saved feedback", suggestions: "Suggestions", wording: "Wording style", saveStyle: "Save wording style",
    sharingOn: "AI sharing on", sharingOff: "AI sharing off", saved: "Saved phrases",
    save: "Save phrase", review: "Use sentence", edit: "Edit", reject: "Reject", none: "None of these",
    ai: "AI suggestion", savedSource: "Saved phrase", consentOff: "Turn off AI sharing?",
    consentOn: "Turn on AI sharing? When you ask for suggestions, MyWords sends your keywords, language style, partner tone and context, and up to three saved phrases and two feedback records you allowed for AI reuse to the configured AI provider. Displayed AI replies are saved in data/ai_log.txt on this computer.",
    partnerTone: "Partner tone", correction: "Optional corrected sentence", saveFeedback: "Save this feedback on this computer?",
    reuseFeedback: "Allow the saved feedback to be used in future AI requests?",
    reusePhrase: "Allow this saved phrase to be used in future AI requests?",
    empty: "No matching saved phrases. You can type your own sentence.",
    rejectHelp: "Choose a reason. You can write a correction and decide whether to save your feedback.",
    close: "Close", remove: "Delete", report: "Review and export selected phrases",
    reviewReport: "Download this report?", confirmText: "Confirm this wording?",
    saveConfirmed: "Save the confirmed sentence as a phrase?", deletePerson: "Delete this person and the saved conversation data for them?",
    deletePhrase: "Delete this saved phrase?", ready: "Saved.", unavailable: "Start the Java app to use MyWords."
  },
  ms: {
    feedback: "Maklum balas disimpan", suggestions: "Cadangan", wording: "Gaya ayat", saveStyle: "Simpan gaya ayat",
    sharingOn: "Perkongsian AI aktif", sharingOff: "Perkongsian AI dimatikan", saved: "Ayat disimpan",
    save: "Simpan ayat", review: "Pilih ayat", edit: "Sunting", reject: "Tolak", none: "Tiada yang sesuai",
    ai: "Cadangan AI", savedSource: "Ayat disimpan", consentOff: "Matikan perkongsian AI?",
    consentOn: "Aktifkan perkongsian AI? Apabila anda meminta cadangan, MyWords menghantar kata kunci, gaya bahasa, nada dan konteks teman bicara, serta sehingga tiga ayat dan dua rekod maklum balas yang anda benarkan untuk digunakan semula kepada penyedia AI. Jawapan AI yang dipaparkan disimpan dalam data/ai_log.txt pada komputer ini.",
    partnerTone: "Nada teman bicara", correction: "Ayat pembetulan (pilihan)", saveFeedback: "Simpan maklum balas pada komputer ini?",
    reuseFeedback: "Benarkan maklum balas digunakan semula dalam permintaan AI?",
    reusePhrase: "Benarkan ayat ini digunakan semula dalam permintaan AI?",
    empty: "Tiada ayat yang sepadan. Anda boleh menaip ayat sendiri.",
    rejectHelp: "Pilih sebab. Anda boleh menulis pembetulan dan memilih sama ada mahu menyimpan maklum balas.",
    close: "Tutup", remove: "Padam", report: "Semak dan eksport ayat dipilih",
    reviewReport: "Muat turun laporan ini?", confirmText: "Sahkan ayat ini?",
    saveConfirmed: "Simpan ayat yang disahkan?", deletePerson: "Padam orang ini dan data perbualan mereka?",
    deletePhrase: "Padam ayat ini?", ready: "Disimpan.", unavailable: "Jalankan aplikasi Java untuk menggunakan MyWords."
  },
  zh: {
    feedback: "已保存的反馈", suggestions: "建议", wording: "措辞风格", saveStyle: "保存措辞风格",
    sharingOn: "已开启 AI 分享", sharingOff: "已关闭 AI 分享", saved: "已保存的句子",
    save: "保存句子", review: "选择句子", edit: "编辑", reject: "拒绝", none: "都不合适",
    ai: "AI 建议", savedSource: "已保存的句子", consentOff: "关闭 AI 分享？",
    consentOn: "开启 AI 分享？请求建议时，MyWords 会向配置的 AI 服务发送关键词、语言风格、对话者的语气和场景，以及您允许重复使用的最多三条句子和两条反馈。显示的 AI 回复会保存到本机的 data/ai_log.txt。",
    partnerTone: "对话者的语气", correction: "更正后的句子（可选）", saveFeedback: "将此反馈保存到本机？",
    reuseFeedback: "允许在将来的 AI 请求中使用此反馈？", reusePhrase: "允许在将来的 AI 请求中使用此句子？",
    empty: "没有匹配的已保存句子。您可以输入自己的句子。",
    rejectHelp: "选择原因。您可以写下更正并决定是否保存反馈。",
    close: "关闭", remove: "删除", report: "检查并导出所选句子",
    reviewReport: "下载此报告？", confirmText: "确认此措辞？", saveConfirmed: "保存已确认的句子？",
    deletePerson: "删除此人及其已保存的对话数据？", deletePhrase: "删除此句子？",
    ready: "已保存。", unavailable: "请启动 Java 应用以使用 MyWords。"
  }
};

let suggestionVersion = 0;
let suggestionRequest = "";
let rejectionIndex = -1;
let resultMessage = "";
let preferenceQueue = Promise.resolve();
const bt = () => browserText[state.language] || browserText.en;
const byId = (id) => document.getElementById(id);

async function javaAction(action, fields = {}) {
  if (location.protocol !== "http:") throw new Error(bt().unavailable);
  const response = await fetch("/api/" + action, {
    method: "POST",
    body: new URLSearchParams(fields)
  });
  const data = await response.json();
  if (!response.ok) throw new Error(data.error || bt().unavailable);
  return data;
}

function displayError(error) {
  showToast(error.message || bt().unavailable, "error");
}

function updateBrowserLabels() {
  const copy = bt();
  const labels = {
    "browser-ai-title": copy.suggestions, "browser-style-label": copy.wording,
    "browser-save-style": copy.saveStyle, "browser-manage-phrases": copy.saved,
    "browser-manage-feedback": copy.feedback,
    "browser-save-manual": copy.save, "browser-partner-tone-label": copy.partnerTone,
    "browser-reject-correction-label": copy.correction, "reject-modal-subtitle": copy.rejectHelp,
    "reject-suggestions-btn-label": copy.none
  };
  for (const [id, label] of Object.entries(labels)) byId(id).textContent = label;
  const sharing = state.currentUser?.aiSharing === true;
  for (const id of ["browser-sharing", "browser-settings-sharing"]) {
    byId(id).textContent = sharing ? copy.sharingOn : copy.sharingOff;
    byId(id).setAttribute("aria-pressed", String(sharing));
  }
}

function showAiSettings() {
  const style = state.currentUser.style || "Casual rojak";
  const select = byId("browser-style");
  const fallback = select.querySelector("[data-current-style]");
  if (fallback) fallback.remove();
  if (![...select.options].some(option => option.value === style)) {
    const option = new Option(style, style);
    option.dataset.currentStyle = "true";
    select.add(option);
  }
  select.value = style;
  updateBrowserLabels();
}

async function loadBrowserSession() {
  updateBrowserLabels();
  try {
    const { user } = await javaAction("session");
    if (user) {
      loginUser(user);
      updateBrowserLabels();
    }
  } catch (error) {
    showAuthError(error.message);
  }
}

async function handleAuthSubmit(event) {
  event.preventDefault();
  const signup = state.authMode === "signup";
  const password = byId("auth-password").value;
  if (signup && password !== byId("auth-confirm-password").value) {
    showAuthError(t().errPasswordMismatch);
    return;
  }
  try {
    const { user } = await javaAction(signup ? "signup" : "login", {
      email: byId("auth-email").value, password,
      name: byId("auth-name").value, gender: byId("auth-gender").value
    });
    byId("auth-form").reset();
    loginUser(user);
    updateBrowserLabels();
    showToast(signup ? t().toastWelcomeNew : t().toastWelcomeBack(user.name), "success");
  } catch (error) {
    showAuthError(error.message);
  }
}

async function handleDemoLogin() {
  try {
    const { user } = await javaAction("demo");
    loginUser(user);
    updateBrowserLabels();
    showToast(t().toastDemoLogin, "success");
  } catch (error) {
    showAuthError(error.message);
  }
}

async function handleLogout() {
  try {
    await javaAction("logout");
    closeProfileMenu();
    stopEmergencyAlarm();
    stopSpeaking();
    clearBrowserSuggestions();
    state.currentUser = null;
    byId("app-view").classList.add("hidden");
    byId("auth-view").classList.remove("hidden");
    byId("auth-form").reset();
    byId("auth-error").classList.add("hidden");
    switchAuthTab("login");
    showToast(t().toastLoggedOut, "info");
  } catch (error) {
    displayError(error);
  }
}

function saveBrowserPreferences(changes) {
  const email = state.currentUser?.email;
  const request = preferenceQueue.then(async () => {
    const user = state.currentUser;
    if (!user || user.email !== email) return;
    const fields = { style: user.style, tone: user.tone, gender: user.gender,
      language: user.language, theme: user.theme, font: user.fontSize, ...changes };
    const response = await javaAction("profile", fields);
    if (state.currentUser?.email === email) state.currentUser = response.user;
  });
  preferenceQueue = request.catch(() => {});
  return request;
}

async function setFontSize(sizeKey) {
  closeFontSizeMenu();
  try {
    if (state.currentUser) await saveBrowserPreferences({ font: sizeKey });
    applyFontSize(sizeKey);
  } catch (error) {
    displayError(error);
  }
}

async function setThemeMode(theme) {
  try {
    if (state.currentUser) await saveBrowserPreferences({ theme });
    applyThemeMode(theme);
  } catch (error) {
    displayError(error);
  }
}

async function setAppLanguage(code) {
  const language = TRANSLATIONS[code] ? code : "en";
  try {
    if (state.currentUser) await saveBrowserPreferences({ language });
    state.language = language;
    document.documentElement.lang = t().htmlLang;
    syncLanguageButtonsUI();
    applyAllTranslations();
    updateBrowserLabels();
    if (state.currentUser && state.selectedPartnerCategory) {
      renderMembersPanel(state.selectedPartnerCategory, state.currentUser.members[state.selectedPartnerCategory] || []);
    }
    if (state.selectedPerson) {
      const person = state.selectedPerson;
      byId("selected-person-display").textContent = t().talkingWith(person.name, person.role);
      byId("selected-person-phrase-preview").textContent = getLocalizedCallPhrase(person);
      byId("call-btn-label").textContent = t().callToMySideBtn(person.name);
    }
    if (state.selectedNeedCategory && state.selectedPerson) selectNeedCategory(state.selectedNeedCategory);
    if (state.suggestions.length) renderBrowserCards(state.suggestions);
    showToast(t().toastLangUpdated(LANGUAGE_DISPLAY_NAMES[language] || language), "info");
  } catch (error) {
    displayError(error);
  }
}

async function handleUpdateGender(event) {
  event.preventDefault();
  try {
    await saveBrowserPreferences({ gender: byId("settings-gender-input").value });
    showToast(bt().ready, "success");
  } catch (error) {
    displayError(error);
  }
}

async function handleUpdateUsername(event) {
  event.preventDefault();
  try {
    const result = await javaAction("name", { name: byId("settings-username-input").value });
    state.currentUser = result.user;
    updateUserHeaderDisplay();
    showToast(bt().ready, "success");
  } catch (error) {
    displayError(error);
  }
}

async function handleUpdatePassword(event) {
  event.preventDefault();
  const replacement = byId("settings-new-password").value;
  const errorBox = byId("settings-password-error");
  errorBox.classList.add("hidden");
  try {
    if (replacement !== byId("settings-confirm-password").value) throw new Error(t().errPasswordMismatch);
    await javaAction("password", { current: byId("settings-current-password").value, replacement });
    event.currentTarget?.reset();
    for (const id of ["settings-current-password", "settings-new-password", "settings-confirm-password"]) byId(id).value = "";
    showToast(t().toastPasswordUpdated, "success");
  } catch (error) {
    errorBox.textContent = error.message;
    errorBox.classList.remove("hidden");
  }
}

async function saveBrowserStyle(event) {
  event.preventDefault();
  try {
    await saveBrowserPreferences({ style: byId("browser-style").value });
    showToast(bt().ready, "success");
  } catch (error) {
    displayError(error);
  }
}

async function toggleBrowserSharing() {
  if (!state.currentUser) return;
  const enabled = !state.currentUser.aiSharing;
  if (!confirm(enabled ? bt().consentOn : bt().consentOff)) return;
  try {
    const response = await javaAction("consent", { enabled });
    state.currentUser = response.user;
    updateBrowserLabels();
  } catch (error) {
    displayError(error);
  }
}

async function handleAddMemberSubmit(event) {
  event.preventDefault();
  const category = state.selectedPartnerCategory;
  if (!category) return;
  const partner = state.editingMemberId || "";
  try {
    const result = await javaAction(partner ? "partner-edit" : "partner-add", {
      category, partner, name: byId("member-name").value, role: byId("member-role").value,
      callPhrase: byId("member-phrase").value, tone: byId("browser-partner-tone").value
    });
    state.currentUser = result.user;
    const members = result.user.members[category];
    const saved = members.find(person => person.id === result.partnerId);
    closeAddMemberModal();
    updateCategoryCounts();
    renderMembersPanel(category, members);
    selectPersonToTalkWith({ ...saved, category });
    showToast(bt().ready, "success");
  } catch (error) {
    displayError(error);
  }
}

async function deleteMember(category, partner) {
  if (!confirm(bt().deletePerson)) return;
  try {
    const result = await javaAction("partner-delete", { partner });
    state.currentUser = result.user;
    updateCategoryCounts();
    if (state.selectedPerson?.id === partner) resetPersonSelection();
    else renderMembersPanel(category, result.user.members[category]);
  } catch (error) {
    displayError(error);
  }
}

async function handleAddCommonNeedSubmit(event) {
  event.preventDefault();
  const topic = state.selectedNeedCategory;
  const need = state.editingCommonNeedId || "";
  try {
    const result = await javaAction(need ? "need-edit" : "need-add", { topic, need, label: byId("common-need-label").value });
    state.currentUser = result.user;
    closeAddCommonNeedModal();
    selectNeedCategory(topic);
    showToast(bt().ready, "success");
  } catch (error) {
    displayError(error);
  }
}

async function deleteCommonNeed(topic, need) {
  if (!confirm(bt().deletePhrase)) return;
  try {
    const result = await javaAction("need-delete", { topic, need });
    state.currentUser = result.user;
    if (state.selectedNeedCategory === topic) selectNeedCategory(topic);
  } catch (error) {
    displayError(error);
  }
}

function clearBrowserSuggestions() {
  suggestionVersion++;
  suggestionRequest = "";
  state.suggestions = [];
  resultMessage = "";
  byId("suggestions-list").replaceChildren();
  byId("ai-loading-state").classList.add("hidden");
  byId("ai-learned-banner").classList.add("hidden");
}

async function generateAndRenderSentences() {
  if (!state.selectedPerson || !state.selectedIntent) return;
  clearBrowserSuggestions();
  const version = suggestionVersion;
  const person = state.selectedPerson;
  const keywords = state.selectedIntent;
  const section = byId("step3-section");
  section.classList.remove("hidden");
  byId("ai-loading-state").classList.remove("hidden");
  byId("step3-context-subtitle").textContent = t().step3Context(person.name, person.role,
    getNeedCategoryLabel(state.selectedNeedCategory), keywords);
  updateStepIndicator(3);
  updateBrowserLabels();
  const fixedHeader = document.querySelector("#app-view header");
  const stepBar = byId("step-progress-bar");
  section.style.scrollMarginTop = ((fixedHeader?.offsetHeight || 0) + (stepBar?.offsetHeight || 0) + 12) + "px";
  section.scrollIntoView({ behavior: "smooth", block: "start" });
  try {
    const result = await javaAction("suggest", { partner: person.id, keywords });
    if (version !== suggestionVersion) return;
    suggestionRequest = result.request;
    state.suggestions = result.suggestions;
    resultMessage = result.message || (result.suggestions.length ? "" : bt().empty);
    renderBrowserCards(result.suggestions);
  } catch (error) {
    if (version === suggestionVersion) {
      resultMessage = error.message;
      renderBrowserCards([]);
    }
  } finally {
    if (version === suggestionVersion) byId("ai-loading-state").classList.add("hidden");
  }
}

function uiButton(label, action, primary = false) {
  const button = document.createElement("button");
  button.type = "button";
  button.className = primary
    ? "touch-target px-5 rounded-2xl bg-brand-700 hover:bg-brand-800 text-white font-extrabold"
    : "touch-target px-5 rounded-2xl border-2 border-slate-300 dark:border-slate-600 font-bold";
  button.textContent = label;
  button.addEventListener("click", action);
  return button;
}

function renderBrowserCards(suggestions) {
  const list = byId("suggestions-list");
  const banner = byId("ai-learned-banner");
  byId("ai-learned-text").textContent = resultMessage;
  banner.classList.toggle("hidden", !resultMessage);
  list.replaceChildren();
  for (let index = 0; index < suggestions.length; index++) {
    const choice = suggestions[index];
    const card = document.createElement("div");
    card.className = "p-5 rounded-2xl border-2 border-slate-200 dark:border-slate-700 bg-slate-50 dark:bg-slate-800/80 flex flex-col lg:flex-row gap-4 justify-between";
    const copy = document.createElement("div");
    copy.className = "space-y-1.5";
    const badge = document.createElement("span");
    badge.className = "px-3 py-0.5 rounded-lg bg-brand-100 text-brand-800 font-extrabold";
    badge.textContent = choice.source === "AI" ? bt().ai : bt().savedSource;
    const sentence = document.createElement("p");
    sentence.className = "text-2xl font-extrabold";
    sentence.textContent = choice.text;
    copy.append(badge, sentence);
    const actions = document.createElement("div");
    actions.className = "flex flex-wrap gap-2";
    actions.append(uiButton(t().speakOutLoudBtn, (event) => speakText(choice.text, event.currentTarget), true),
      uiButton(t().showScreenBtn, () => openFullscreenShow(choice.text)),
      uiButton(bt().review, () => confirmBrowserChoice(index, false)),
      uiButton(bt().edit, () => confirmBrowserChoice(index, true)),
      uiButton(bt().reject, () => openRejectModal(index)));
    card.append(copy, actions);
    list.append(card);
  }
}

function discardBrowserSuggestions() {
  clearBrowserSuggestions();
  byId("custom-sentence-input").focus();
}

async function confirmBrowserChoice(index, edit) {
  const choice = state.suggestions[index];
  if (!choice) return;
  const text = edit ? prompt(bt().confirmText, choice.text) : choice.text;
  if (!text?.trim() || !confirm(bt().confirmText + "\n\n" + text)) return;
  const savePhrase = (choice.source !== "SAVED" || text !== choice.text) && confirm(bt().saveConfirmed);
  const reusePhrase = savePhrase && confirm(bt().reusePhrase);
  const saveFeedback = confirm(bt().saveFeedback);
  const reuseFeedback = saveFeedback && confirm(bt().reuseFeedback);
  await sendBrowserDecision(index, { decision: edit ? "EDIT" : "APPROVE", text,
    savePhrase, reusePhrase, saveFeedback, reuseFeedback });
}

function openRejectModal(index) {
  rejectionIndex = index;
  byId("other-reject-input").value = "";
  byId("browser-reject-correction").value = "";
  byId("reject-modal").classList.remove("hidden");
  byId("reject-reason-1").focus();
}

function closeRejectModal() {
  rejectionIndex = -1;
  byId("reject-modal").classList.add("hidden");
}

async function submitRejectionReason(reason, detail = "") {
  const index = rejectionIndex;
  if (index < 0) return;
  const text = byId("browser-reject-correction").value.trim();
  const saveFeedback = confirm(bt().saveFeedback);
  const reuseFeedback = saveFeedback && !!text && confirm(bt().reuseFeedback);
  const code = reason.startsWith("Wrong meaning") ? "WRONG_MEANING"
    : /formal|blunt/i.test(reason) ? "WRONG_TONE" : "OTHER_UNSURE";
  closeRejectModal();
  await sendBrowserDecision(index, { decision: "REJECT", reason: code, detail, text, saveFeedback, reuseFeedback });
}

function submitCustomRejectionReason() {
  const detail = byId("other-reject-input").value.trim();
  if (detail) submitRejectionReason("Other", detail);
  else showToast(t().toastTapOrTypeReason, "error");
}

async function sendBrowserDecision(index, fields) {
  const version = suggestionVersion;
  try {
    await javaAction("decision", { request: suggestionRequest, index, ...fields });
    if (version !== suggestionVersion) return;
    clearBrowserSuggestions();
    if (fields.decision !== "REJECT") {
      byId("custom-sentence-input").value = fields.text;
      byId("custom-sentence-input").focus();
    }
    if (fields.saveFeedback || fields.savePhrase) showToast(bt().ready, "success");
  } catch (error) {
    displayError(error);
  }
}

async function saveManualPhrase() {
  const person = state.selectedPerson;
  const text = byId("custom-sentence-input").value.trim();
  if (!text || !person) {
    showToast(t().toastTypeSentenceFirst, "error");
    return;
  }
  if (!confirm(bt().confirmText + "\n\n" + text)) return;
  try {
    await javaAction("manual", { partner: person.id, text, keywords: state.selectedIntent || text.slice(0, 300),
      reuse: confirm(bt().reusePhrase) });
    showToast(bt().ready, "success");
  } catch (error) {
    displayError(error);
  }
}

async function openBrowserPhrases() {
  const partner = state.selectedPerson?.id;
  if (!partner) return;
  try {
    const result = await javaAction("phrases", { partner });
    if (state.selectedPerson?.id !== partner) return;
    renderPhraseDialog(result.phrases, partner);
  } catch (error) {
    displayError(error);
  }
}

function renderPhraseDialog(phrases, partner) {
  let dialog = byId("browser-phrases-dialog");
  if (!dialog) {
    dialog = document.createElement("dialog");
    dialog.id = "browser-phrases-dialog";
    dialog.className = "w-full max-w-2xl p-6 rounded-2xl text-slate-900 bg-white dark:bg-slate-900 dark:text-white";
    dialog.setAttribute("aria-labelledby", "browser-phrases-title");
    document.body.append(dialog);
  }
  dialog.replaceChildren();
  const heading = document.createElement("h2");
  heading.id = "browser-phrases-title";
  heading.className = "text-2xl font-extrabold mb-4";
  heading.textContent = bt().saved;
  dialog.append(heading);
  const checkboxes = [];
  for (const phrase of phrases) {
    const row = document.createElement("div");
    row.className = "p-3 mb-2 border-b-2 border-slate-200 flex flex-wrap gap-3 items-center";
    const label = document.createElement("label");
    const input = document.createElement("input");
    input.type = "checkbox";
    input.value = phrase.id;
    checkboxes.push(input);
    label.append(input, " " + phrase.text);
    const change = async (action, fields) => {
      try {
        const result = await javaAction(action, { partner, phrase: phrase.id, ...fields });
        renderPhraseDialog(result.phrases, partner);
      } catch (error) {
        displayError(error);
      }
    };
    row.append(label, uiButton((phrase.reuse ? "✓ " : "") + bt().reusePhrase,
      () => change("phrase-reuse", { reuse: !phrase.reuse })),
    uiButton(bt().remove, () => { if (confirm(bt().deletePhrase)) change("phrase-delete", {}); }));
    dialog.append(row);
  }
  if (!phrases.length) {
    const empty = document.createElement("p");
    empty.textContent = bt().empty;
    dialog.append(empty);
  }
  if (phrases.length) {
    dialog.append(uiButton(bt().report, () => exportBrowserReport(partner, checkboxes)));
  }
  dialog.append(uiButton(bt().close, () => dialog.close()));
  if (!dialog.open) dialog.showModal();
}

async function exportBrowserReport(partner, checkboxes) {
  const selected = checkboxes.filter(box => box.checked).map(box => box.value).join(",");
  try {
    const result = await javaAction("report", { partner, selected });
    if (!confirm(bt().reviewReport + "\n\n" + result.report)) return;
    const url = URL.createObjectURL(new Blob([result.report], { type: "text/plain;charset=UTF-8" }));
    const link = document.createElement("a");
    link.href = url;
    link.download = "mywords-report.txt";
    link.click();
    setTimeout(() => URL.revokeObjectURL(url), 1000);
  } catch (error) {
    displayError(error);
  }
}

function stopSpeaking() {
  activeTtsRequestId++;
  if ("speechSynthesis" in window) window.speechSynthesis.cancel();
  if (activeTtsTriggerBtn) activeTtsTriggerBtn.classList.remove("speaking-active");
  activeTtsTriggerBtn = null;
  byId("stop-speech-btn")?.classList.add("hidden");
}

async function speakText(text, triggerButton = null) {
  if (!text || !("speechSynthesis" in window)) {
    showToast(t().toastTtsUnsupported, "error");
    return;
  }
  stopSpeaking();
  const requestId = activeTtsRequestId;
  const initial = speechSynthesis.getVoices();
  const voices = (initial.length ? initial : await waitForBrowserVoices()).filter(voice => voice.localService);
  if (requestId !== activeTtsRequestId) return;
  const { voice } = getBestBrowserVoice(state.language, state.currentUser?.gender !== "Female", voices);
  if (!voice) {
    showToast(t().toastTtsUnsupported, "error");
    return;
  }
  const utterance = new SpeechSynthesisUtterance(text);
  utterance.voice = voice;
  utterance.lang = voice.lang;
  utterance.rate = state.speechRate;
  utterance.onend = utterance.onerror = () => { if (requestId === activeTtsRequestId) stopSpeaking(); };
  activeTtsTriggerBtn = triggerButton;
  triggerButton?.classList.add("speaking-active");
  byId("stop-speech-btn").classList.remove("hidden");
  speechSynthesis.speak(utterance);
}

async function openBrowserFeedback() {
  const partner = state.selectedPerson?.id;
  if (!partner) return;
  let dialog = byId("browser-feedback-dialog");
  if (!dialog) {
    dialog = document.createElement("dialog");
    dialog.id = "browser-feedback-dialog";
    dialog.className = "w-full max-w-2xl p-6 rounded-2xl text-slate-900 bg-white dark:bg-slate-900 dark:text-white";
    dialog.setAttribute("aria-labelledby", "browser-feedback-title");
    document.body.append(dialog);
  }
  const render = (feedback) => {
    dialog.replaceChildren();
    const heading = document.createElement("h2");
    heading.id = "browser-feedback-title";
    heading.className = "text-2xl font-extrabold mb-4";
    heading.textContent = bt().feedback;
    dialog.append(heading);
    for (const item of feedback) {
      const row = document.createElement("div");
      row.className = "p-3 mb-2 border-b-2 border-slate-200 flex flex-wrap gap-3 items-center";
      const wording = document.createElement("p");
      wording.textContent = item.confirmed && item.confirmed !== item.text
        ? item.text + " → " + item.confirmed : item.text;
      row.append(wording);
      if (item.detail) {
        const detail = document.createElement("p");
        detail.textContent = item.detail;
        row.append(detail);
      }
      const update = async (action, fields) => {
        try {
          const result = await javaAction(action, { partner, feedback: item.id, ...fields });
          render(result.feedback);
        } catch (error) {
          displayError(error);
        }
      };
      if (item.decision !== "REJECT" || item.confirmed) {
        row.append(uiButton((item.reuse ? "✓ " : "") + bt().reuseFeedback,
          () => update("feedback-reuse", { reuse: !item.reuse })));
      }
      row.append(uiButton(bt().remove, () => {
        if (confirm(bt().remove + "?")) update("feedback-delete", {});
      }));
      dialog.append(row);
    }
    dialog.append(uiButton(bt().close, () => dialog.close()));
    if (!dialog.open) dialog.showModal();
  };
  try {
    const result = await javaAction("feedback", { partner });
    if (state.selectedPerson?.id === partner) render(result.feedback);
  } catch (error) {
    displayError(error);
  }
}
