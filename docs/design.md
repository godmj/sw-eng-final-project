# 설계문서 (최신: v0.3, 2026-11-25)

> v0.1 대비 변경: game/에 ConsoleChooser, ConsolePrinter, SimpleEnemyAI, Game 추가(커밋 cf25a86). 4절·6절·8절 갱신.

## 1. 개요
- 주제: Java 콘솔 턴제 RPG (카테고리 1). 플레이어 파티 vs 적 그룹, 3라운드, 마지막 적은 왕(드래곤).
- 분담: 신민지 = 게임 흐름·턴 순서·입출력·상태이상 진행·api/ / 정찬호 = 캐릭터(전사, 드래곤)·전투 규칙(데미지·치명타·명중·무기·스킬·상태이상 내용)
- 저장소: godmj/sw-eng-final-project (폴더: api/, game/, characters/, docs/)

## 2. 구조 (폴리모피즘 플랫폼 = Combatant)
- 게임은 `Combatant` 타입만 안다. 플레이어/적 구분 없이 `List<Combatant>` 하나를 한 루프로 돈다.
- 캐릭터는 화면에 출력하지 않는다. 결과는 `ActionResult` 값으로 돌려주고, 출력은 게임이 한다.

## 3. api/ (두 사람이 합의한 뒤에만 수정)
| 파일 | 역할 |
|---|---|
| Combatant | 조회(이름·HP·속도·선공등급), onTurnStart, attack, defend, getSkills, useSkill, takeDamage, takeTrueDamage |
| ActionContext | 행동마다 넘기는 정보: hitModifier(명중 보정 %), Random(시드 고정용) |
| ActionResult | 행동 결과 값: 행동자, 대상, 행동명, 수치, 회복 여부, 명중, 치명타, 걸린 상태이상 |
| SkillInfo, TargetType | 스킬 메뉴용 정보(이름, 대상 ENEMY/ALLY/SELF, 사용 가능 여부) |
| StatusEffect | 상태이상. 내용은 캐릭터가 정의, 보관·적용은 게임이 한다 |

## 4. game/ (신민지)
| 파일 | 역할 |
|---|---|
| BattleEngine | 전투 한 판 진행. 턴 순서 정렬, 턴 루프, 상태이상 적용, 종료 판정 |
| ActionChooser, Decision | 행동 선택을 엔진에서 분리(콘솔 입력 / 적 AI / 자동 테스트) |
| BattleListener | 진행 상황을 알리는 출력 쪽 인터페이스 |
| BattleOutcome | PLAYER_WIN / ENEMY_WIN / DRAW |
| FakeCombatant | 엔진 테스트용 가짜 캐릭터 |
| ConsoleChooser | 플레이어 입력(1 공격 / 2 방어 / 3 스킬, 대상 선택). 잘못된 입력은 재질문, 스킬 메뉴에서 0으로 뒤로, 입력 종료 시 자동 방어 |
| ConsolePrinter | BattleListener 콘솔 구현. 모든 화면 출력은 여기서만 |
| SimpleEnemyAI | 적 행동: 살아 있는 상대 중 무작위 기본 공격 |
| Game | 스테이지 3개를 이어서 진행. 파티 HP는 스테이지 간 유지, 패배 시 GAME OVER, 마지막 스테이지 적 = 왕 |

## 5. 합의한 규칙
- 턴 순서: priority 내림차순 → speed 내림차순 → 파티 순서. 전투 시작 때 한 번만 정렬.
- 방어: 다음 자기 턴 시작(onTurnStart)에 해제.
- 상태이상: 같은 이름이면 중첩 없이 지속시간만 갱신. 속도를 바꾸는 효과는 두지 않음(재정렬 방지). 독·화상은 takeTrueDamage(방어 무시).
- 라운드 상한 도달 시 양 팀 HP 합 비율로 승패, 같으면 무승부.
- 난수는 ActionContext의 Random만 사용.
- 전투 규칙(수치, 스킬, 피해 계산 순서, 상태이상 보관 분담)은 `docs/rules.md`를 기준으로 한다.
- 파티 3인(전사·궁수·사제), 스테이지 사이 HP 유지, 적은 방어하지 않음(2026-11-25 확정).

## 6. 검증 현황
- FakeCombatant로 선공 순서, 전멸 승패, 라운드 상한 판정, 사망 알림 확인.
- 3스테이지 전체 흐름(클리어), 잘못된 입력 재질문, 스킬 메뉴 뒤로, 입력 종료 시 자동 방어 확인.
- 미검증: 상태이상 피해·명중 보정 합산(상태이상을 거는 실제 캐릭터가 필요).

## 7. 알려진 불일치
- 정찬호의 characters/ 1단계 커밋은 구 ActionResult(2인자 생성자, getDamage)를 써서 api/와 컴파일되지 않는다. 3곳 교체 요청 전달함(2026-11-25).

## 8. 다음 작업
- 실행용 Main(실제 Warrior/Dragon 연결, 찬호님 characters/ 수정 반영 후).
- 적 AI 고도화(스킬 사용), 파티 다인 구성 시 대상 선택 점검.
- SRS: 유스케이스 다이어그램, 클래스 다이어그램.
