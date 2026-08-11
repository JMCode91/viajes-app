import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-placeholder',
  templateUrl: './placeholder.html',
  styleUrl: './placeholder.css',
})
export class Placeholder {
  private readonly route = inject(ActivatedRoute);

  protected readonly titulo: string =
    (this.route.snapshot.data['titulo'] as string | undefined) ??
    'Próximamente';
}