import { ChangeDetectionStrategy, Component } from '@angular/core';
import { environment } from '@env/environment';

@Component({
  selector: 'cs-whatsapp-fab',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <a
      [href]="link"
      target="_blank"
      rel="noopener noreferrer"
      class="fab"
      aria-label="Chat on WhatsApp"
    >
      <svg viewBox="0 0 24 24" width="28" height="28" aria-hidden="true">
        <path
          fill="#fff"
          d="M20.52 3.48A11.86 11.86 0 0012 0C5.37 0 0 5.37 0 12a11.93 11.93 0 001.64 6L0 24l6.18-1.62A12 12 0 1020.52 3.48zm-8.5 18.36a9.86 9.86 0 01-5.05-1.38l-.36-.21-3.67.96.98-3.58-.23-.37A9.94 9.94 0 1112.02 21.84zM17.4 14.4c-.3-.15-1.76-.87-2.03-.97-.27-.1-.47-.15-.66.15s-.76.97-.93 1.17c-.17.2-.34.22-.63.07a8.16 8.16 0 01-4.02-3.52c-.3-.52.3-.48.86-1.6.1-.2.05-.37-.02-.52-.07-.15-.66-1.58-.9-2.17-.24-.58-.49-.5-.66-.5h-.57c-.2 0-.52.07-.8.37s-1.05 1.02-1.05 2.48 1.07 2.88 1.22 3.07c.15.2 2.1 3.2 5.1 4.5 1.78.78 2.48.84 3.37.7.54-.08 1.66-.68 1.9-1.34.23-.66.23-1.22.16-1.34-.07-.13-.27-.2-.57-.35z"
        />
      </svg>
    </a>
  `,
  styles: [
    `
      .fab {
        position: fixed;
        right: 1.25rem;
        bottom: 1.25rem;
        width: 60px;
        height: 60px;
        border-radius: 50%;
        background: linear-gradient(145deg, #2fe671 0%, #20b858 100%);
        color: #fff;
        display: grid;
        place-items: center;
        box-shadow: 0 12px 32px rgba(37, 211, 102, 0.45), inset 0 2px 2px rgba(255, 255, 255, 0.6);
        z-index: 90;
        transition: transform 0.35s cubic-bezier(0.23, 1, 0.32, 1), box-shadow 0.35s ease, background 0.3s ease;
        animation: cs-pulse-ring 2.4s ease-out infinite;
        transform-style: preserve-3d;
      }
      .fab::before {
        content: '';
        position: absolute;
        inset: -6px;
        border-radius: 50%;
        border: 2px solid rgba(37, 211, 102, 0.45);
        animation: cs-fab-ring 2.4s ease-out infinite;
      }
      .fab::after {
        content: '';
        position: absolute;
        top: 2px; left: 10px; right: 10px; height: 16px;
        border-radius: 50%;
        background: linear-gradient(180deg, rgba(255, 255, 255, 0.6) 0%, transparent 100%);
        pointer-events: none;
      }
      .fab:hover {
        transform: translateY(-6px) scale(1.12) rotate(-8deg);
        box-shadow: 0 20px 40px rgba(37, 211, 102, 0.6), 0 0 20px rgba(47, 230, 113, 0.4);
      }
      .fab svg {
        position: relative;
        z-index: 1;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.15));
        transition: transform 0.3s ease;
      }
      .fab:hover svg {
        transform: scale(1.08);
      }
      @keyframes cs-fab-ring {
        0%   { transform: scale(1);   opacity: 0.7; }
        70%  { transform: scale(1.4); opacity: 0; }
        100% { transform: scale(1.4); opacity: 0; }
      }
      @media (max-width: 768px) {
        .fab { right: 0.85rem; bottom: 0.85rem; width: 54px; height: 54px; }
      }
      @media (prefers-reduced-motion: reduce) {
        .fab, .fab::before { animation: none; }
      }
    `,
  ],
})
export class WhatsappFabComponent {
  readonly link = `https://wa.me/${environment.whatsappNumber}?text=${encodeURIComponent(
    'Hi, I would like an enquiry about civil supplies.',
  )}`;
}
