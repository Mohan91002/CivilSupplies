import { Directive, ElementRef, HostListener, Input, Renderer2, OnInit } from '@angular/core';

@Directive({
  selector: '[csTilt3D]',
  standalone: true,
})
export class Tilt3DDirective implements OnInit {
  @Input() maxTilt = 12; // Maximum tilt angle in degrees
  @Input() perspective = 1000; // Perspective in pixels
  @Input() scale = 1.03; // Scale factor on hover
  @Input() enableGlare = true; // Enable specular glare reflection

  private glareElement: HTMLElement | null = null;

  constructor(private el: ElementRef<HTMLElement>, private renderer: Renderer2) {}

  ngOnInit(): void {
    const host = this.el.nativeElement;
    this.renderer.setStyle(host, 'transform-style', 'preserve-3d');
    this.renderer.setStyle(host, 'transition', 'transform 0.4s cubic-bezier(0.23, 1, 0.32, 1), box-shadow 0.4s ease');
    this.renderer.setStyle(host, 'will-change', 'transform');

    if (this.enableGlare) {
      this.setupGlare();
    }
  }

  private setupGlare(): void {
    const host = this.el.nativeElement;
    this.renderer.setStyle(host, 'position', 'relative');
    this.renderer.setStyle(host, 'overflow', 'hidden');

    this.glareElement = this.renderer.createElement('div');
    this.renderer.setStyle(this.glareElement, 'position', 'absolute');
    this.renderer.setStyle(this.glareElement, 'top', '0');
    this.renderer.setStyle(this.glareElement, 'left', '0');
    this.renderer.setStyle(this.glareElement, 'width', '100%');
    this.renderer.setStyle(this.glareElement, 'height', '100%');
    this.renderer.setStyle(this.glareElement, 'pointer-events', 'none');
    this.renderer.setStyle(this.glareElement, 'background', 'radial-gradient(circle at 50% 50%, rgba(255,255,255,0.25) 0%, rgba(255,255,255,0) 80%)');
    this.renderer.setStyle(this.glareElement, 'opacity', '0');
    this.renderer.setStyle(this.glareElement, 'transition', 'opacity 0.3s ease');
    this.renderer.setStyle(this.glareElement, 'border-radius', 'inherit');
    this.renderer.setStyle(this.glareElement, 'z-index', '3');

    this.renderer.appendChild(host, this.glareElement);
  }

  @HostListener('mousemove', ['$event'])
  onMouseMove(event: MouseEvent): void {
    const rect = this.el.nativeElement.getBoundingClientRect();
    const x = event.clientX - rect.left;
    const y = event.clientY - rect.top;

    const width = rect.width;
    const height = rect.height;

    const rotateX = ((y / height) - 0.5) * -this.maxTilt * 2;
    const rotateY = ((x / width) - 0.5) * this.maxTilt * 2;

    this.renderer.setStyle(
      this.el.nativeElement,
      'transform',
      `perspective(${this.perspective}px) rotateX(${rotateX.toFixed(2)}deg) rotateY(${rotateY.toFixed(2)}deg) scale3d(${this.scale}, ${this.scale}, ${this.scale})`
    );

    if (this.glareElement) {
      const glareX = (x / width) * 100;
      const glareY = (y / height) * 100;
      this.renderer.setStyle(
        this.glareElement,
        'background',
        `radial-gradient(circle at ${glareX}% ${glareY}%, rgba(255,255,255,0.28) 0%, rgba(255,255,255,0) 70%)`
      );
      this.renderer.setStyle(this.glareElement, 'opacity', '1');
    }
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    this.renderer.setStyle(
      this.el.nativeElement,
      'transform',
      `perspective(${this.perspective}px) rotateX(0deg) rotateY(0deg) scale3d(1, 1, 1)`
    );

    if (this.glareElement) {
      this.renderer.setStyle(this.glareElement, 'opacity', '0');
    }
  }
}
