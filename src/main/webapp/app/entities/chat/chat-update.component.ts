import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { IChat, Chat } from 'app/shared/model/chat.model';
import { ChatService } from './chat.service';

@Component({
  selector: 'jhi-chat-update',
  templateUrl: './chat-update.component.html'
})
export class ChatUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    message: [],
    srcUser: [],
    dstUser: [],
    date: []
  });

  constructor(protected chatService: ChatService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ chat }) => {
      this.updateForm(chat);
    });
  }

  updateForm(chat: IChat): void {
    this.editForm.patchValue({
      id: chat.id,
      message: chat.message,
      srcUser: chat.srcUser,
      dstUser: chat.dstUser,
      date: chat.date != null ? chat.date.format(DATE_TIME_FORMAT) : null
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const chat = this.createFromForm();
    if (chat.id !== undefined) {
      this.subscribeToSaveResponse(this.chatService.update(chat));
    } else {
      this.subscribeToSaveResponse(this.chatService.create(chat));
    }
  }

  private createFromForm(): IChat {
    return {
      ...new Chat(),
      id: this.editForm.get(['id'])!.value,
      message: this.editForm.get(['message'])!.value,
      srcUser: this.editForm.get(['srcUser'])!.value,
      dstUser: this.editForm.get(['dstUser'])!.value,
      date: this.editForm.get(['date'])!.value != null ? moment(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IChat>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }
}
