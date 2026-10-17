# Design direction

Composed with `create-frontend` (design-direction.js, type `events`, name
`NitteSync`) for the existing Jakarta Faces UI. The stack is fixed by the
project brief (JSF, no JavaScript frameworks), so this records the visual
direction, not a stack choice.

**Campus bulletin.** The Citrus palette is arranged like a printed campus
noticeboard: warm paper, aubergine ink, a single tangerine spot colour, hard
editorial rules, date stamps and one typographic ticket. The interface should
feel posted rather than generated - confident type, a visible grid, and
ornament that earns its place.

| Axis | Choice | Reason |
|---|---|---|
| Theme | Citrus (library), light only | Campus events app for students and clubs - the theme library maps this project type to Citrus: tangerine action on warm off-white, aubergine ink, grape accent |
| Hero | split + typographic ticket | index.xhtml reads as copy beside one action; the ticket ("Find your people.") is the only ornament and carries no data, so it is `aria-hidden` |
| Navigation | inline, active underline | At most six links, chosen by the signed-in role; a collapsed menu would hide the app's primary navigation behind an extra tap. The current page is marked with `aria-current` |
| Rhythm | rule-led columns | Sections open with a small uppercase eyebrow and close with a 1-2px ink rule; the event list is a card grid, not invented sections |
| Cards | elevated + date stamp | Citrus shape is soft elevation at 8/12/20px radii; the event card leads with a day/month/year stamp so a scan of the board starts with dates |
| Buttons | soft (rounded) | Follows the theme radius; one filled action per view, secondary actions are outlined, destructive actions are outlined in `danger` |
| Type | all-sans / editorial | Red Hat Display (headings) with Red Hat Text (body) - one voice at two weights. Display headings are tight (-0.04em) and large; labels are 11px uppercase with 0.12-0.16em tracking |
| Imagery | none; typographic poster | No image column in the schema; the public, login and register pages get a drawn asterisk/ticket motif instead of stock photography |
| Motion | functional + staged entrance | Each view gets one short rise/fade (page head, hero, cards, tables, next-event rows), plus a 4px card lift, arrow nudge, button lift and a rotating brand mark on hover. No looping motion; `prefers-reduced-motion` removes the entrances and all hover movement |
| Density | regular | Card grid plus data tables; 70rem container, 24px gutters on desktop and 20px on a phone |

Alternatives offered: **(B)** Birch · showcase · rules · tinted cards ·
all-sans/tight; **(C)** Gallery · editorial · columns · flat cards ·
all-serif/display.

## Tokens

`src/main/webapp/resources/css/style.css` holds the palette, type scale,
spacing, radii, shadows and motion tokens as CSS custom properties. Components
reference tokens only; no literal colours outside `:root`. Soft status fills
and hovers are mixed from the tokens with `color-mix()`, so changing the palette
still changes every surface.

Motion: 120 / 200 / 320 ms for micro-interactions, with 240-400 ms entrances,
`ease-out` for entrances, `ease-in` for exits and 4px travel - all collapsed
or removed under `prefers-reduced-motion: reduce`. Entrance keyframes animate
the individual `translate` property, so the final animation state never blocks
the hover `transform` on the same card or button.

The mark is an eight-spoke asterisk on a tangerine tile (`resources/img/favicon.svg`,
with a PNG fallback). It is a simple original shape, not a third-party logo.

Login and registration share the split auth card: a dark poster panel carrying
the asterisk and one line of copy, beside the form. Registration adds a role
selector (student or club) and a confirmation field; the service rejects any
other role, so an administrator account cannot be created through the form.

## Contrast and accessibility

Checked with the skill's `scripts/check-contrast.js` against the Citrus token
set (light mode, `pass: true`):

- body text 13.62:1 · lowest text pair 5.08:1 (`successForeground` on `success`)
- lowest UI pair 3.06:1 (`input` on `background`)

Applied in the stylesheet: focus rings are drawn with a 2px offset in
`background` (the ring is close to `primary` in lightness); status colours are
used as fills carrying their own foreground, or as a 3px rule on a tint of the
same status colour with `foreground` text, never as status-coloured text on a
`muted` fill.

Verified in the browser at 375 and 1280 wide: axe-core reports 0 violations on
all eleven pages, no page overflows horizontally, wide tables scroll inside
their own box, and the keyboard order starts with a skip link and keeps a
visible focus ring. Entrance animations are absent under
`prefers-reduced-motion: reduce`.
