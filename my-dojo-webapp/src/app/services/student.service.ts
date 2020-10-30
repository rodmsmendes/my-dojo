import { Injectable } from '@angular/core';
import { formatDate } from '@angular/common'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators'
import { Student } from '../models/student';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient) { }

  private studentsUrl = 'http://localhost:8080/api/v1/students'
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-type': 'application/json'
    })
  }

  private log(message: any){
    console.log(message)
  }

  public add(student: Student): Observable<Student> {
    let studentBody = Object.assign(student)
    studentBody.dateOfBirth = formatDate(student.dateOfBirth, 'yyyy-MM-dd', 'en')
    return this.http.post<Student>(this.studentsUrl, student, this.httpOptions)
  }
}
