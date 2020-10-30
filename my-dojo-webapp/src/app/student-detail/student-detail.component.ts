import { Component, OnInit } from '@angular/core';
import { Student } from '../models/student';
import { StudentService } from '../services/student.service';

@Component({
  selector: 'app-student-detail',
  templateUrl: './student-detail.component.html',
  styleUrls: ['./student-detail.component.css']
})
export class StudentDetailComponent implements OnInit {

  student: Student = {
    id: 1,
    name: 'Rodolfo Sevilhano',
    dateOfBirth: new Date(1984, 8 - 1, 9, 0, 0, 0, 0),
    phoneNumber: '(34) 99973-9360',
    email: 'rodolfo.m.s.mendes@gmail.com',
    gradeType: 'DAN',
    gradeRank: 1,
    guardianEmail: '',
    guardianName: '',
    guardianPhone: ''
  }

  gradeTypes = [
    {value: "KYU", label: "Kyu"},
    {value: "DAN", label: "Dan"}
  ]

  gradeRanks = []

  days = []

  months = [
    {value: 1,  label: "Jan"},
    {value: 2,  label: "Feb"},
    {value: 3,  label: "Mar"},
    {value: 4,  label: "Apr"},
    {value: 5,  label: "May"},
    {value: 6,  label: "Jun"},
    {value: 7,  label: "Jul"},
    {value: 8,  label: "Aug"},
    {value: 9,  label: "Sep"},
    {value: 10, label: "Oct"},
    {value: 11, label: "Nov"},
    {value: 12, label: "Dec"}
  ]

  years = []

  selectedDay = 23

  constructor(private studentService: StudentService) {
    for(var i = 1; i <= 10; i++) {
      this.gradeRanks.push(i)
    }

    for(var i = 1; i <= 31; i++) {
      this.days.push(i)
    }

    let currentYear = new Date().getFullYear();
    for(var i = currentYear - 6; i >= currentYear - 100; i--) {
      this.years.push(i)
    }
  }

  ngOnInit(): void {
  }

  onChangeDay(event:number): void {
    if (this.student && this.student.dateOfBirth) {
      this.student.dateOfBirth.setDate(event)
    }
  }

  onChangeMonth(event:number): void {
    if (this.student && this.student.dateOfBirth) {
      this.student.dateOfBirth.setMonth(event - 1)
    }
  } 

  onChangeYear(event:number): void {
    if (this.student && this.student.dateOfBirth) {
      this.student.dateOfBirth.setFullYear(event)
    }
  }

  onClick(event:any): void {
    this.studentService
      .add(this.student)
      .subscribe(response => console.log(response))
  }
}
