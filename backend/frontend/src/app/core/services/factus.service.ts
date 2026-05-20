import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface FactusEmitirRequest {
  idFactura: number;
  tipoDocumento: '13' | '31';   // 13=CC, 31=NIT
  numeroDocumento: string;
  nombreComprador: string;
  emailComprador?: string;
  direccion?: string;
  digitoVerificacion?: string;  // Solo para NIT
  telefono?: string;
}

export interface FactusEmitirResponse {
  exito: boolean;
  cufe: string | null;
  numeroFactura: string | null;
  mensaje: string;
  qrUrl: string | null;
}

@Injectable({ providedIn: 'root' })
export class FactusService {
  private readonly apiUrl = `${environment.apiUrl}/factus`;

  constructor(private http: HttpClient) {}

  emitir(request: FactusEmitirRequest): Observable<FactusEmitirResponse> {
    return this.http.post<FactusEmitirResponse>(`${this.apiUrl}/emitir`, request);
  }
}
